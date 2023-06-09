package com.coding.informer.dictionary_app_v3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class BookmarkActivity : AppCompatActivity() {

    var bookmarkedWordsList : TextView? = null;
    var bookmarksPageBtn: Button? = null;
    var mainPageBtn: Button? = null;
    var mRequestQueue : RequestQueue? = null;
    var mStringRequest : StringRequest? = null;
    var searchWord: String = "";
    var definitionListStr: String = "";
    var dialogBuilder: AlertDialog.Builder? = null;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bookmark)

        bookmarksPageBtn = findViewById<Button>(R.id.bookmarksPageBtn)

        mainPageBtn = findViewById<Button>(R.id.mainPageBtn)

        lifecycleScope.launch {
            fetchBookmarkedWordsViaSupabase()
        }

        mainPageBtn!!.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.addItemDecoration(SimpleItemDecoration(this))
        val layoutManager = LinearLayoutManager(this@BookmarkActivity)
        recyclerView.layoutManager = layoutManager

        val defaultItems: MutableList<WordObject>? = ArrayList<WordObject>()
        defaultItems?.add(WordObject("Word1","Null"))
        defaultItems?.add(WordObject("Word2","Null"))
        defaultItems?.add(WordObject("Word3","Null"))
        defaultItems?.add(WordObject("Word4","Null"))
        defaultItems?.add(WordObject("Word5","Null"))

        val adapter = RecyclerViewAdapter(this@BookmarkActivity, defaultItems!!)
        recyclerView.adapter = adapter
    }

    private suspend fun fetchBookmarkedWordsViaSupabase() {

        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.supabase_project_url,
            supabaseKey = BuildConfig.supabase_project_api_key,
        ) {
            install(GoTrue) {

            }
            install(Postgrest) {

            }
        }

        var result: PostgrestResult = client.postgrest["bookmarked_words"].select(columns = Columns.list("bookmarked_word", "created_at"))
        var bookmarkedWordsStr: String = ""
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val items: MutableList<WordObject> = ArrayList<WordObject>()

        for (bookmarked_word in result.body?.jsonArray!!) {
            val formatted_date: Date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(bookmarked_word.jsonObject.get("created_at").toString().replace("T", " ").replace("\"", "").slice(IntRange(0, 19)))

            items?.add(WordObject(bookmarked_word.jsonObject.get("bookmarked_word").toString().replace("\"", ""), formatted_date.toString()))
        }

        val adapter = RecyclerViewAdapter(this@BookmarkActivity, items!!)
        recyclerView.adapter = adapter
        bookmarkedWordsList?.text = bookmarkedWordsStr
        Log.d("Supabase-kt Postgrest: ", result.body.toString())
    }

    private fun callDictionaryAPI(): JSONArray? {

        mRequestQueue = Volley.newRequestQueue(this)
        var responseJSONArray : JSONArray? = null;
        mStringRequest = StringRequest(
            Request.Method.GET, Api.DICTIONARY_BASE_URL + "/" + searchWord,
            { response ->
                Toast.makeText(applicationContext, "Search word: $searchWord", Toast.LENGTH_LONG)
                    .show()
                val jsonResponse = JSONArray(response);
//Syntax for traversing jsonResponse in order to extract definitions:
                val meaningsObj = (jsonResponse.get(0) as JSONObject).getJSONArray("meanings");
                responseJSONArray = meaningsObj

                Log.d("API Response", response)
            }
        ) { error ->
            Toast.makeText(applicationContext, "Ran into error during API Request", Toast.LENGTH_LONG)
                .show()
        }
        mRequestQueue!!.add(mStringRequest)
        return responseJSONArray
    }


    private fun returnListItems() {

        var bookmarkedJSONArray : JSONArray? = callDictionaryAPI() // <- Make this method await()

        for (i  in 0 until bookmarkedJSONArray?.length()!!) {
                        val meaningsObj2 =
                            (bookmarkedJSONArray.get(i) as JSONObject).getJSONArray("definitions");
                        for (j in 0 until meaningsObj2.length()) {
                            var defObj = (meaningsObj2.get(j) as JSONObject)
                        }
                    }
    }
}