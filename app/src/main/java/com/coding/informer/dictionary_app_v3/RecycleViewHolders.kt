package com.coding.informer.dictionary_app_v3

import android.util.SparseBooleanArray
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    var awardTitle: TextView
    var awardYear: TextView
    var player: TextView
    private val selectedItems = SparseBooleanArray()

    init {
        itemView.setOnClickListener(this)
        awardTitle = itemView.findViewById(R.id.awardTitle)
        awardYear = itemView.findViewById(R.id.awardYear)
        player = itemView.findViewById(R.id.playerName)
    }

    override fun onClick(view: View) {
        if (selectedItems[adapterPosition, false]) {
            selectedItems.delete(adapterPosition)
            view.isSelected = false
        } else {
            selectedItems.put(adapterPosition, true)
            view.isSelected = true
        }
    }
}