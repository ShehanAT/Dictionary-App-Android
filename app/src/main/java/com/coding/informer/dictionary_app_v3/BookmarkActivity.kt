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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

class BookmarkActivity : AppCompatActivity() {

    var bookmarkedWordsList : TextView? = null;
    var bookmarksPageBtn: Button? = null;
    var mainPageBtn: Button? = null;

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
    }

    private suspend fun fetchBookmarkedWordsViaSupabase() {

        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.supabase_project_url,
            supabaseKey = BuildConfig.supabase_project_api_key,
        ) {
            //...

//            install(Storage) {
//
//            }
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
}