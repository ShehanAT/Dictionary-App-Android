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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import io.ktor.util.reflect.instanceOf
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
//    private val textToSpeechEngine: TextToSpeech by lazy {
//        // Pass in context and the listener.
//        TextToSpeech(,
//            TextToSpeech.OnInitListener { status ->
//                // set our locale only if init was success.
//                Log.d("TextToSpeech", "TextToSpeech Status: $status")
//                if (status == TextToSpeech.SUCCESS) {
//                    Log.d("TextToSpeech", "TextToSpeech API Init Success")
//                    textToSpeechEngine.language = Locale.UK
//                } else{
//                    Log.d("TextToSpeech", "TextToSpeech API Init Failure")
//                }
//            })
//
//    }

    open fun showDialog(activity: Activity?, definitionWord: String, definitionText: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_alert_dialog)
        val pronounceBtn = dialog.findViewById<View>(R.id.pronounceBtn) as Button
        val shareOnFacebookBtn = dialog.findViewById<View>(R.id.shareOnFacebookBtn) as Button

        definitionWordItem = dialog.findViewById<View>(R.id.definitionWord) as TextView
        definitionWordItem!!.text = definitionWord
        definitionTextItem = dialog.findViewById<View>(R.id.definitionDescription) as TextView
        definitionTextItem!!.text = definitionText

        pronounceBtn.setOnClickListener {
            var jsonBody : JSONObject = JSONObject()
            jsonBody.put("text", "Test")

            var requestBody : String = jsonBody.toString()
            mRequestQueue = Volley.newRequestQueue(activity.applicationContext)

            var parentActivity : Activity = activity.parent
            if(parentActivity.instanceOf(MainActivity::class)) {
                (parentActivity as MainActivity).callTTSAPI()
            }

//            val request: StringRequest = object : StringRequest(
//                Method.POST, Api.LARGE_TTS_BASE_URL,
//                Response.Listener<String?> { response ->
//                    // inside on response method we are
//                    // hiding our progress bar
//                    // and setting data to edit text as empty
//                    Log.d("API Response", response)
//                },
//                Response.ErrorListener { error -> // method to handle errors.
//                    Log.d("API Response", "Fail to get response = $error")
//                }) {
//                override fun getParams(): Map<String, String>? {
//                    // below line we are creating a map for
//                    // storing our values in key and value pair.
//                    val params: MutableMap<String, String> = HashMap()
//
//                    // on below line we are passing our key
//                    // and value pair to our parameters.
//                    params["text"] = "Test"
//                    // at last we are
//                    // returning our params.
//                    return params
//                }
//            }
//            mRequestQueue!!.add(request)


//            tts = TextToSpeech(this, this)
//            onInit(0)
//            tts!!.speak("Test", TextToSpeech.QUEUE_FLUSH, null, "")
//            TranslatorFactory
//                .instance
//                .with(TranslatorFactory.TRANSLATORS.SPEECH_TO_TEXT,
//                    object : ConversionCallback {
//                        override fun onSuccess(result: String) {
//                            Log.d("TranslatorFactory", "Result: ${result}")
//                        }
//
//                        override fun onCompletion() {
//
//                        }
//
//                        override fun onErrorOccurred(errorMessage: String) {
//                            Log.d("TranslatorFactory", "ErrorMessage: ${errorMessage}")
//                        }
//                    }
//                    ).initialize("Test", activity)
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
//            else {
//                tts!!. = true
//            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }
}