package com.coding.informer.dictionary_app_v3

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class RecyclerViewAdapter(context: Context?, private val itemList: List<WordObject>) :
    RecyclerView.Adapter<RecyclerViewHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, null)
        return RecyclerViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        holder.wordText.setText(itemList[position].word)
        holder.wordAddedDate.setText(itemList[position].dateCreated)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}