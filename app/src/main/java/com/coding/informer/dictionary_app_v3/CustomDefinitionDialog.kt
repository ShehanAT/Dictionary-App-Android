package com.coding.informer.dictionary_app_v3


import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView


class CustomDefinitionDialog  {
    var d: Dialog? = null
    var pronounceBtn: Button? = null
    var shareOnFacebookBtn: Button? = null
    var definitionWordItem: TextView? = null
    var definitionTextItem: TextView?  = null

    open fun showDialog(activity: Activity?, definitionWord: String, definitionText: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_alert_dialog)
        val pronounceBtn = dialog.findViewById<View>(R.id.pronounceBtn) as Button
        pronounceBtn
        val shareOnFacebookBtn = dialog.findViewById<View>(R.id.shareOnFacebookBtn) as Button
        shareOnFacebookBtn.setOnClickListener { dialog.dismiss() }
        definitionWordItem = dialog.findViewById<View>(R.id.definitionWord) as TextView
        definitionWordItem!!.text = definitionWord
        definitionTextItem = dialog.findViewById<View>(R.id.definitionDescription) as TextView
        definitionTextItem!!.text = definitionText
        dialog.show()
    }
}