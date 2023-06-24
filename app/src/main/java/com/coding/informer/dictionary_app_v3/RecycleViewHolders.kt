package com.coding.informer.dictionary_app_v3

import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import org.json.JSONArray
import org.json.JSONObject

internal class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    var wordText: TextView
    var wordAddedDate: TextView
    private val selectedItems = SparseBooleanArray()
    var mRequestQueue : RequestQueue? = null;
    var mStringRequest : StringRequest? = null;
    var definitionListStr : String = "";
    var dialogBuilder: AlertDialog.Builder? = null;

    init {
        itemView.setOnClickListener(this)
        wordText = itemView.findViewById(R.id.wordText)
        wordAddedDate = itemView.findViewById(R.id.wordAddedDate)
    }

    override fun onClick(view: View) {
        if (selectedItems[adapterPosition, false]) {
            selectedItems.delete(adapterPosition)
            view.isSelected = false
        } else {
            Log.d("RecyclerView Clicked: ", "RecyclerView Item Clicked!")
            callDictionaryAPI(this.wordText.getText().toString())
            selectedItems.put(adapterPosition, true)
            view.isSelected = true
        }
    }


    private fun callDictionaryAPI(searchWord: String) {
        mRequestQueue = Volley.newRequestQueue(itemView.context)
        mStringRequest = StringRequest(
            Request.Method.GET, Api.DICTIONARY_BASE_URL + "/" + searchWord,
            { response ->
                val jsonResponse = JSONArray(response);
//Syntax for traversing jsonResponse in order to extract definitions:
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
                    dialogBuilder = AlertDialog.Builder(itemView.context)
                    dialogBuilder?.setMessage("Definition Modal")?.setTitle("Definition Modal")
                    var alert: AlertDialog? = dialogBuilder?.create()
                    alert?.setCancelable(true)
                    alert?.setTitle("Definition: $searchWord")
                    alert?.setMessage(definitionListStr)
                    alert?.setOnCancelListener {
                        selectedItems.delete(adapterPosition)
                        itemView.isSelected = false
                    }
                    alert?.show()

                } catch (e : Exception) {
                    Log.d("API Response: ", "Ran into error while parsing API Response")
                }
                Log.d("API Response: ", response)

            }
        ) { error ->
//            var errorAlert: AlertDialog? = dialogBuilder?.create()
            dialogBuilder = AlertDialog.Builder(itemView.context)
            var errorAlert: AlertDialog? = dialogBuilder?.create()
            errorAlert?.setCancelable(true)
            errorAlert?.setTitle("Word Not Found")
            errorAlert?.setMessage("Sorry, we were not able to find any definitions for that word. Please try again...")
            errorAlert?.setOnCancelListener {
                    selectedItems.delete(adapterPosition)
                    itemView.isSelected = false
            }
            errorAlert?.show()
            Log.d("API Response", "Word not found in Dictionary API")
        }
        mRequestQueue!!.add(mStringRequest)
    }
}