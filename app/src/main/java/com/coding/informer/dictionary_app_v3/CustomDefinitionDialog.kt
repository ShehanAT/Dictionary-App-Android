package com.coding.informer.dictionary_app_v3


import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import java.util.Locale



class CustomDefinitionDialog  {
    var d: Dialog? = null
    var pronounceBtn: Button? = null
    var shareOnFacebookBtn: Button? = null
    var definitionWordItem: TextView? = null
    var definitionTextItem: TextView?  = null

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
            dialog.hide()
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
}