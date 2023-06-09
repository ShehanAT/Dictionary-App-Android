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
import java.util.ArrayList

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

        bookmarkedWordsList = findViewById<TextView>(R.id.bookmarkedWordsList)

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
        val posts: List<WordObject>? = returnListItems()
        val adapter = RecyclerViewAdapter(this@BookmarkActivity, posts!!)
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

        var result: PostgrestResult = client.postgrest["bookmarked_words"].select(columns = Columns.list("bookmarked_word"))
        var bookmarkedWordsStr: String = ""
        for (bookmarked_word in result.body?.jsonArray!!) {
            bookmarkedWordsStr += bookmarked_word.jsonObject.get("bookmarked_word")
            bookmarkedWordsStr += "\n"
        }
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
// (((jsonResponse.get(0) as JSONObject).getJSONArray("meanings").get(0) as JSONObject).getJSONArray("definitions").get(0) as JSONObject).getString("definition")
                val meaningsObj = (jsonResponse.get(0) as JSONObject).getJSONArray("meanings");
                responseJSONArray = meaningsObj
//                try{
//                    definitionListStr = "\n";
//                    for (i  in 0 until meaningsObj.length()) {
//                        val meaningsObj2 =
//                            (meaningsObj.get(i) as JSONObject).getJSONArray("definitions");
//                        for (j in 0 until meaningsObj2.length()) {
//                            var defObj = (meaningsObj2.get(j) as JSONObject)
//                            definitionListStr += "* " + (defObj.getString("definition")) + "\n";
//                        }
//                    }
//
//                    var alert: AlertDialog? = dialogBuilder?.create()
//                    alert?.setCancelable(true)
//                    alert?.setTitle("Definition: $searchWord")
//                    alert?.setMessage(definitionListStr)
//                    alert?.show()
//
//                } catch (e : Exception) {
//                    Log.d("API Response:", "Ran into error while parsing API Response")
//                }

                Log.d("API Response", response)
            }
        ) { error ->
            Toast.makeText(applicationContext, "Ran into error during API Request", Toast.LENGTH_LONG)
                .show()
        }
        mRequestQueue!!.add(mStringRequest)
        return responseJSONArray
    }


    private fun returnListItems(): List<WordObject>? {
        var bookmarkedJSONArray : JSONArray? = callDictionaryAPI() // <- Make this method await()
        val items: MutableList<WordObject> = ArrayList<WordObject>()
        for (i  in 0 until bookmarkedJSONArray?.length()!!) {
                        val meaningsObj2 =
                            (bookmarkedJSONArray.get(i) as JSONObject).getJSONArray("definitions");
                        for (j in 0 until meaningsObj2.length()) {
                            var defObj = (meaningsObj2.get(j) as JSONObject)
                            items.add(WordObject(defObj.getString("definition")))
//                            definitionListStr += "* " + (defObj.getString("definition")) + "\n";
                        }
                    }


//        items.add(WordObject("Cristiano Ronaldo"))
//        items.add(WordObject("Lionel Messi"))
//        items.add(WordObject("Cristiano Ronaldo"))
//        items.add(WordObject("Luca Modric"))
//        items.add(WordObject("Haven't decided yet"))
        return items
    }
}