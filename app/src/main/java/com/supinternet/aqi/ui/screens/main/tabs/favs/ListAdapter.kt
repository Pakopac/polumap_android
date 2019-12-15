package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.supinternet.aqi.data.network.model.station.Data

class ListAdapter(private val list: List<Data>)
    : RecyclerView.Adapter<DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DataViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data: Data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

}