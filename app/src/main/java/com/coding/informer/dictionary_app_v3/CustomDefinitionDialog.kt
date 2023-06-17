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
    
//    override fun onCreate(savedInstanceState: Bundle) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.custom_alert_dialog)
//        shareOnFacebookBtn = findViewById<View>(R.id.pronounceBtn) as Button
//        shareOnFacebookBtn = findViewById<View>(R.id.shareOnFacebookBtn) as Button
//        pronounceBtn!!.setOnClickListener(this)
//        shareOnFacebookBtn!!.setOnClickListener(this)
//    }

//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.shareOnFacebookBtn -> c.finish()
//            R.id.shareOnFacebookBtn -> dismiss()
//            else -> {}
//        }
//        dismiss()
//    }
    open fun showDialog(activity: Activity?, definitionWord: String, definitionText: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_alert_dialog)
        val pronounceBtn = dialog.findViewById<View>(R.id.pronounceBtn) as Button
        pronounceBtn
        val shareOnFacebookBtn = dialog.findViewById<View>(R.id.shareOnFacebookBtn) as Button
        shareOnFacebookBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}