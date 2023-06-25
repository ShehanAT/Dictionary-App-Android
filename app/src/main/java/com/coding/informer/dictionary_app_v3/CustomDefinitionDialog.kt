package com.coding.informer.dictionary_app_v3


import android.R.attr.name
import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class CustomDefinitionDialog : AppCompatActivity(), TextToSpeech.OnInitListener  {
    var d: Dialog? = null
    var pronounceBtn: Button? = null
    var shareOnFacebookBtn: Button? = null
    var definitionWordItem: TextView? = null
    var definitionTextItem: TextView?  = null
    var tts : TextToSpeech? = null
    var mRequestQueue : RequestQueue? = null;
    var mStringRequest : StringRequest? = null;
    var bookmarkBtn : Button? = null;
    var toastMessage: TextView? = null;
    var closeBtn : ImageButton? = null;

    open fun showDialog(activity: Activity?, definitionWord: String, definitionText: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_alert_dialog)
        val pronounceBtn = dialog.findViewById<View>(R.id.pronounceBtn) as Button
        val shareOnFacebookBtn = dialog.findViewById<View>(R.id.shareOnFacebookBtn) as Button

        toastMessage = dialog.findViewById<View>(R.id.toastMessage) as TextView
        toastMessage!!.text = ""

        closeBtn = dialog.findViewById<View>(R.id.closeBtn) as ImageButton
        closeBtn!!.setOnClickListener {
            dialog.hide()
        }

        definitionWordItem = dialog.findViewById<View>(R.id.definitionWord) as TextView
        definitionWordItem!!.text = definitionWord
        definitionTextItem = dialog.findViewById<View>(R.id.definitionDescription) as TextView
        definitionTextItem!!.text = definitionText

        bookmarkBtn = dialog.findViewById<View>(R.id.bookmarkBtn) as Button

        bookmarkBtn!!.setOnClickListener {
            var wordToBookmark = definitionWord
            lifecycleScope.launch {
                if (activity != null) {
                    var parentActivity: Activity = activity
                    if (parentActivity.instanceOf(MainActivity::class)) {
                        (parentActivity as MainActivity).addWordToBookmarkedWordSupabaseTable(
                            wordToBookmark
                        )
                        toastMessage!!.text = "Word bookmarked successfully!"
                    }
                }
            }
        }



        pronounceBtn.setOnClickListener {
            var jsonBody: JSONObject = JSONObject()
            jsonBody.put("text", "Test")

            var requestBody: String = jsonBody.toString()
            mRequestQueue = Volley.newRequestQueue(activity.applicationContext)

            if (activity != null) {
                var parentActivity: Activity = activity
                if (parentActivity.instanceOf(MainActivity::class)) {
                    (parentActivity as MainActivity).callTTSAPI()
                }
            }
        }

        shareOnFacebookBtn.setOnClickListener {
            val shareDialog: ShareDialog = ShareDialog(activity)
            val shareContent: ShareLinkContent = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://api.dictionaryapi.dev/api/v2/entries/en/$definitionWord"))
                .setQuote("Definition: $definitionWord\n$definitionText")
                .build()

            shareDialog.show(shareContent)
        }

        dialog.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)



    }
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)
            Log.d("TTS", "Result: ${result}")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }
}