package com.coding.informer.dictionary_app_v3

//import io.github.jan.supabase.storage.BucketApi
//import io.github.jan.supabase.storage.Storage
//import io.github.jan.supabase.storage.storage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
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
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    var apiResponseView: TextView? = null;
    var mRequestQueue : RequestQueue? = null;
    var mStringRequest : StringRequest? = null;
    var searchWordTextInput : TextInputEditText? = null;
    var searchWord : String? = "grain";
    var searchButton : Button? = null;
    var definitionList : ArrayList<String> = ArrayList();
    var definitionListStr : String = "";
    var pronunciationBtn : Button? = null;
    var bookmarkBtn : Button? = null;
    var bookmarksPageBtn : Button? = null;
    var historyPageBtn : Button? = null;
    var dialogBuilder: AlertDialog.Builder? = null;

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                Log.d("TextToSpeech", "TextToSpeech Status: $status")
                if (status == TextToSpeech.SUCCESS) {
                    Log.d("TextToSpeech", "TextToSpeech API Init Success")
                    textToSpeechEngine.language = Locale.UK
                } else{
                    Log.d("TextToSpeech", "TextToSpeech API Init Failure")
                }
            })

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        searchWordTextInput = findViewById<TextInputEditText>(R.id.searchWordTextInput)

        pronunciationBtn = findViewById<Button>(R.id.pronunciationBtn)

        searchButton = findViewById<Button>(R.id.searchButton)

        bookmarkBtn = findViewById<Button>(R.id.bookmarkBtn)

        bookmarksPageBtn = findViewById<Button>(R.id.bookmarksPageBtn)

        historyPageBtn = findViewById<Button>(R.id.historyPageBtn)

        searchWordTextInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                searchWord = s.toString();
            }
        })

        searchButton!!.setOnClickListener {
            lifecycleScope.launch {
                addWordToHistorySupabaseTable(searchWord!!);
            }
            callDictionaryAPI();
        }

        pronunciationBtn!!.setOnClickListener {
            val text = searchWordTextInput!!.text.toString().trim();

            if (text.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeechEngine!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
                } else {
                    textToSpeechEngine!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                }
            } else {
                Toast.makeText(this, "Text cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        bookmarkBtn!!.setOnClickListener {
            var wordToBookmark = searchWordTextInput!!.text.toString().trim()
            lifecycleScope.launch {
                addWordToBookmarkedWordSupabaseTable(wordToBookmark)
            }
            Toast.makeText(this, "Bookmarked word $wordToBookmark successfully!", Toast.LENGTH_LONG).show()
        }

        bookmarksPageBtn!!.setOnClickListener {
            startActivity(Intent(this, BookmarkActivity::class.java))

        }

        historyPageBtn!!.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder?.setMessage("Definition Modal")?.setTitle("Definition Modal")



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                    }
                }
            }
        }
    }

    public suspend fun addWordToBookmarkedWordSupabaseTable(wordToAdd: String) {
        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.supabase_project_url,
            supabaseKey = BuildConfig.supabase_project_api_key,
        ) {
            install(GoTrue) {

            }
            install(Postgrest) {

            }
        }

        var result: PostgrestResult = client.postgrest["bookmarked_words"].insert(
            value = mapOf("bookmarked_word" to wordToAdd),
            upsert = true
        )

        Log.d("Supabase-kt Postgrest: ", result.body.toString())
    }

    public suspend fun addWordToHistorySupabaseTable(word: String) {
        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.supabase_project_url,
            supabaseKey = BuildConfig.supabase_project_api_key,
        ) {
            install(GoTrue) {

            }
            install(Postgrest) {

            }
        }

        var result: PostgrestResult = client.postgrest["word_history"].insert(
            value = mapOf("word" to word),
            upsert = true
        )

        Log.d("Supabase-kt Postgrest: ", result.body.toString())
    }

    private fun callDictionaryAPI() {
        mRequestQueue = Volley.newRequestQueue(this)

        mStringRequest = StringRequest(
            Request.Method.GET, Api.DICTIONARY_BASE_URL + "/" + searchWord,
            { response ->
                Toast.makeText(applicationContext, "Search word: $searchWord", Toast.LENGTH_LONG)
                    .show()
                val jsonResponse = JSONArray(response);
//Syntax for traversing jsonResponse in order to extract definitions:
// (((jsonResponse.get(0) as JSONObject).getJSONArray("meanings").get(0) as JSONObject).getJSONArray("definitions").get(0) as JSONObject).getString("definition")
                val meaningsObj = (jsonResponse.get(0) as JSONObject).getJSONArray("meanings");
                try{
                    definitionListStr = "\n";
                    for (i  in 0 until meaningsObj.length()) {
                        val meaningsObj2 =
                            (meaningsObj.get(i) as JSONObject).getJSONArray("definitions");
                        for (j in 0 until meaningsObj2.length()) {
                            var defObj = (meaningsObj2.get(j) as JSONObject)
                            definitionListStr += "* " + (defObj.getString("definition")) + "\n";
                        }
                    }

                    var alert: AlertDialog? = dialogBuilder?.create()
                    alert?.setCancelable(true)
                    alert?.setTitle("Definition: $searchWord")
                    alert?.setMessage(definitionListStr)
                    alert?.show()

                } catch (e : Exception) {
                    Log.d("API Response:", "Ran into error while parsing API Response")
                }
                Log.d("API Response", response)

            }
        ) { error ->
            Toast.makeText(applicationContext, "Ran into error during API Request", Toast.LENGTH_LONG)
                .show()
        }
        mRequestQueue!!.add(mStringRequest)
    }

    override fun onPause() {
        textToSpeechEngine?.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine?.shutdown()
        super.onDestroy()
    }
}