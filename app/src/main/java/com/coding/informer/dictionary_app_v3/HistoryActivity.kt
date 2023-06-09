package com.coding.informer.dictionary_app_v3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class HistoryActivity: AppCompatActivity() {
    var historyList : TextView? = null;
    var mainPageBtn: Button? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        mainPageBtn = findViewById<Button>(R.id.mainPageBtn)

        mainPageBtn!!.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        lifecycleScope.launch {
            fetchHistoryWordsViaSupabase()
        }

    }

    private suspend fun fetchHistoryWordsViaSupabase() {

        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.supabase_project_url,
            supabaseKey = BuildConfig.supabase_project_api_key,
        ) {
            install(GoTrue) {

            }
            install(Postgrest) {

            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.addItemDecoration(SimpleItemDecoration(this))
        val layoutManager = LinearLayoutManager(this@HistoryActivity)
        recyclerView.layoutManager = layoutManager

        var result: PostgrestResult = client.postgrest["word_history"].select(columns = Columns.list("word", "created_at"))
        var historyWordsStr: String = ""


        val items: MutableList<WordObject> = ArrayList<WordObject>()

        for (history_word in result.body?.jsonArray!!) {
            val formatted_date: Date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(history_word.jsonObject.get("created_at").toString().replace("T", " ").replace("\"", "").slice(IntRange(0, 19)))

            items?.add(WordObject(history_word.jsonObject.get("word").toString().replace("\"", ""), formatted_date.toString()))
        }

        val adapter = RecyclerViewAdapter(this@HistoryActivity, items!!)
        recyclerView.adapter = adapter
        historyList?.text = historyWordsStr
        Log.d("Supabase-kt Postgrest: ", result.body.toString())
    }
}