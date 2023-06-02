package com.coding.informer.dictionary_app_v3

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.launch

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

//    val applicationInfo : ApplicationInfo = application.packageManager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)
//    val supabase_project_url = applicationInfo.metaData["supabase_project_url"].toString()
//    val supabase_project_api_key = applicationInfo.metaData["supabase_project_api_key"].toString()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        apiResponseView = findViewById<TextView>(R.id.apiResponseText)

        searchWordTextInput = findViewById<TextInputEditText>(R.id.searchWordTextInput)

        pronunciationBtn = findViewById<Button>(R.id.pronunciationBtn)

        searchButton = findViewById<Button>(R.id.searchButton)

        bookmarkBtn = findViewById<Button>(R.id.bookmarkBtn)

        searchWordTextInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                searchWord = s.toString();
            }
        })

        searchButton!!.setOnClickListener {
            callDictionaryAPI();
        }

        pronunciationBtn!!.setOnClickListener {
            val text = searchWordTextInput!!.text.toString().trim();
//            tts = tts ?: TextToSpeech.createOrThrow(applicationContext)


            if (text.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    textToSpeechEngine!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
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
            // Save words using Supabase(1st option) or Room(2nd option)


        }

        lifecycleScope.launch {
            connectToSupabase()
        }
    }


    private suspend fun connectToSupabase() {

        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.supabase_project_url,
            supabaseKey = BuildConfig.supabase_project_api_key,

        ) {
            HttpHeaders.Authorization
            //...

            install(Storage) {

            }

        }

//        client.storage.createBucket(id = "bookmarked_words")
        val bookmarked_works_bucket : BucketApi = client.storage.get(bucketId = "bookmarked_words")
        bookmarked_works_bucket.upload("", "sampleWord".toByteArray())
        Log.d("Supabase Storage: ", bookmarked_works_bucket.toString())
//        {
//            public = true
//            fileSizeLimit = 5.megabytes
//            author
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
//                        et_text_input.setText(recognizedText)
                    }
                }
            }
        }
    }

    private fun callDictionaryAPI() {
        mRequestQueue = Volley.newRequestQueue(this)

        mStringRequest = StringRequest(
            Request.Method.GET, Api.DICTIONARY_BASE_URL + "/" + searchWord,
            { response ->
                Toast.makeText(applicationContext, "Search word: " + searchWord, Toast.LENGTH_LONG)
                    .show()
                val jsonResponse = JSONArray(response);
//Syntax for traversing jsonResponse in order to extract definitions:
// (((jsonResponse.get(0) as JSONObject).getJSONArray("meanings").get(0) as JSONObject).getJSONArray("definitions").get(0) as JSONObject).getString("definition")
                val meaningsObj = (jsonResponse.get(0) as JSONObject).getJSONArray("meanings");
                try{
                    definitionListStr = "";
                    for (i  in 0 until meaningsObj.length()) {
                        val meaningsObj2 = (meaningsObj.get(i) as JSONObject).getJSONArray("definitions");
                        for ( j in 0 until meaningsObj2.length()) {
                            var defObj = (meaningsObj2.get(j) as JSONObject)
                            definitionListStr += "* " + (defObj.getString("definition")) + "\n";
                        }
                    }
                    Log.d("Definition List:", definitionList.toList().toString())
//                    val definitionListView = findViewById<RecyclerView>(R.id.definitionList)
                    apiResponseView = findViewById<View>(R.id.apiResponseText) as TextView?
                    apiResponseView?.text = definitionListStr;

                } catch (e : Exception) {
                    Log.d("API Response:", "Ran into error while parsing API Response")
                }

//                for (JSONObject defObj : (((jsonResponse.get(0) as JSONObject).getJSONArray("meanings").get(0) as JSONObject).getJSONArray("definitions").get(0) as JSONObject)) {
//
//                }


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