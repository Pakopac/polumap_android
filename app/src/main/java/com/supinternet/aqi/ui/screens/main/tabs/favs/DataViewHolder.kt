package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.model.station.Data
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

data class Delete(val status: String)

interface API {
    @GET("removeFav")

    fun getDataById(@Query("id") id: String, @Query ("userID") userID: String): Call<Delete>
}
var gson = GsonBuilder()
    .setLenient()
    .create()


val api = Retrofit.Builder()
    .baseUrl("https://us-central1-polumapproject.cloudfunctions.net/api/")
    .addConverterFactory(
        GsonConverterFactory.create(gson)
    )
    .build()
    .create(API::class.java)

fun getAPI(id: String, userID: String, callback: Callback<Delete>) {
    return api.getDataById(id, userID).enqueue(callback)
}

class DataViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_favs_station_card, parent, false)) {
    private var name: TextView? = null
    private var aqi: TextView? = null
    private var date: TextView? = null
    private var cardView: CardView? = null
    private var delete: TextView? = null


    init {
        cardView = itemView.findViewById(R.id.cardView_station)
        name = itemView.findViewById(R.id.maps_tab_station_name)
        aqi = itemView.findViewById(R.id.maps_tab_station_aqi_value)
        date = itemView.findViewById(R.id.maps_tab_station_date_value)
        delete = itemView.findViewById(R.id.delete_fav)
    }

    fun bind(data: Data, user_id: String) {
        delete!!.setOnClickListener { view ->
            getAPI(data.idx.toString(), user_id, object : retrofit2.Callback<Delete>{
                override fun onFailure(call: Call<Delete>, t: Throwable) {
                    Log.e("error", t.message)
                }

                override fun onResponse(call: Call<Delete>, response: Response<Delete>) {
                   Log.v("deleteRequest", "success")
                }

            })
        }

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

