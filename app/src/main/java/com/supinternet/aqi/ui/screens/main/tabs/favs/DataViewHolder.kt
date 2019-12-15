package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.model.station.Data
import java.text.SimpleDateFormat
import java.util.*

class DataViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_maps_station_card, parent, false)) {
    private var name: TextView? = null
    private var aqi: TextView? = null
    private var date: TextView? = null
    private var cardView: CardView? = null


    init {
        cardView = itemView.findViewById(R.id.cardView_station)
        name = itemView.findViewById(R.id.maps_tab_station_name)
        aqi = itemView.findViewById(R.id.maps_tab_station_aqi_value)
        date = itemView.findViewById(R.id.maps_tab_station_date_value)
    }

    fun bind(data: Data) {
        name?.text = data.city.name
        aqi?.text = data.aqi.toString()
        aqi?.setTextColor(
            ContextCompat.getColor(
                name!!.context, when (data.aqi) {
                    in 0..50 -> R.color.aqi_good
                    in 51..100 -> R.color.aqi_moderate
                    in 101..150 -> R.color.aqi_unhealthy_sensitive
                    in 151..200 -> R.color.aqi_unhealthy
                    in 201..300 -> R.color.aqi_very_unhealthy
                    else -> R.color.aqi_hazardous
                }
            )
        )
        date?.text = DateUtils.getRelativeTimeSpanString(
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.ENGLISH
            ).parse(data.time.s).time).toString()
        cardView?.visibility = View.VISIBLE
    }

}

