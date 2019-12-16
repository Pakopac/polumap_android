package com.supinternet.aqi.ui.screens.main.tabs.aroundme

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.supinternet.aqi.R
import com.supinternet.aqi.ui.screens.main.DetailActivity
import com.supinternet.aqi.ui.utils.GoogleMapUtils
import kotlinx.android.synthetic.main.fragment_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat

class StationData(
    val id: Int,
    val name: String?,
    val coordinate: Array<Double>,
    val aqi:String,
    val time:String?
)

class MapsTab : Fragment(), OnMapReadyCallback {

    companion object {
        private val ARG_USERID = "user_id"
        fun newInstance(user_id: String):MapsTab {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_USERID, user_id)
            val fragment = MapsTab()
            fragment.arguments = args
            return fragment
        }
    }

    private val markersData = mutableMapOf<Marker, StationData>()
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("user_id",getArguments().toString())

        (childFragmentManager.findFragmentById(R.id.maps_tab_gmap) as SupportMapFragment).getMapAsync(
            this
        )
    }

    data class Response(
        val data: List<GetData>
    )
    data class GetData(
        val lat: Double,
        val lon: Double,
        val uid: Float,
        val aqi: String,
        val station: Map<String, String>?
    )

    data class ResponseSearch(
        val data: List<GetSearch>
    )

    data class Station(
        val name:String,
        val geo: Array<Double>,
        val url:String,
        val country:String
    )

    data class Time(
        val tz:String,
        val stime:String,
        val vtime:String
    )

    data class GetSearch(
        val uid: Int,
        val aqi: String,
        val time: Time,
        val station: Station
    )

    interface API {
        @GET("getMapBounds")

        fun getDataById(@Query("latlng") id:String) : Call<Response>
    }

    val api = Retrofit.Builder()
        .baseUrl("https://us-central1-polumapproject.cloudfunctions.net/api/")
        .addConverterFactory(
            GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    fun getAPI(latlng: String, callback: Callback<Response>) {
        return api.getDataById(latlng).enqueue(callback)
    }

    interface APISearch {
        @GET("getSearch")
        fun getDataById(@Query("keyword") id:String) : Call<ResponseSearch>
    }

    val apiSearch = Retrofit.Builder()
        .baseUrl("https://us-central1-polumapproject.cloudfunctions.net/api/")
        .addConverterFactory(
            GsonConverterFactory.create())
        .build()
        .create(APISearch::class.java)

    fun getAPISearch(search: String, callback: Callback<ResponseSearch>) {
        return apiSearch.getDataById(search).enqueue(callback)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(), R.raw.map_style
            )
        )

        val btn_search = view!!.findViewById(R.id.maps_tab_search_zone_button) as MaterialButton
        val tab_station = view!!.findViewById(R.id.maps_tab_station) as MaterialCardView
        val name_text =  view!!.findViewById(R.id.maps_tab_station_name) as TextView
        val aqi_value =  view!!.findViewById(R.id.maps_tab_station_aqi_value) as TextView
        val date_value = view!!.findViewById(R.id.maps_tab_station_date_value) as TextView

        Log.v("cardview", btn_search.visibility.toString())

        btn_search.setOnClickListener {
            tab_station.visibility = View.GONE
            val coordinate:String =
                map.getProjection().getVisibleRegion().farLeft.latitude.toString() + ',' +
                        map.getProjection().getVisibleRegion().farLeft.longitude.toString() + ',' +
                        map.getProjection().getVisibleRegion().nearRight.latitude.toString() + ',' +
                        map.getProjection().getVisibleRegion().nearRight.longitude.toString()

            getAPI(coordinate, object : retrofit2.Callback<Response> {
                @TargetApi(Build.VERSION_CODES.O)
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                    map.clear()
                    var count = 0
                    val GetDatas = response.body()
                    if(response.body()?.data.isNullOrEmpty()){
                        val dialogBuilder = AlertDialog.Builder(view?.context)
                        dialogBuilder.setTitle("Aucune station")

                            .setMessage("Aucune station n'existe. Veuillez essayer d'élargir la zone de recheche")
                            .setNegativeButton("OK", DialogInterface.OnClickListener {
                                    dialog, id -> dialog.cancel()
                            })
                            .create()
                            .show()
                    }
                    else{
                        Log.v("abcde", response.body().toString())
                        for(item in response.body()!!.data){
                            if(count < 20) {
                                count++
                                val marker = MarkerOptions()
                                    .position(LatLng(item.lat, item.lon))
                                    .title(item.station?.get("name"))
                                    .icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            GoogleMapUtils.getBitmap(
                                                requireContext(),
                                                R.drawable.ic_map_marker
                                            )
                                        )
                                    )
                                val mapMarker = map.addMarker(marker)
                                val data = StationData(item.uid.toInt(),item.station?.get("name"),
                                    arrayOf(item.lat,item.lon),item.aqi,item.station?.get("time"))
                                markersData[mapMarker] = data
                                map.setOnMarkerClickListener { marker ->
                                    val data = markersData[marker]
                                    if (data != null) {
                                        Log.v("user_id",getArguments().toString())
                                        tab_station.visibility = View.VISIBLE
                                        val intent = Intent(context, DetailActivity::class.java)

                                        maps_tab_station.setOnClickListener{ startActivity(intent)}
                                        Log.v("id", data.id.toString())

                                        intent.putExtra("user_id", getArguments()!!.getString("user_id"))
                                        intent.putExtra("name", data.name)
                                        intent.putExtra("id", data.id.toString())
                                        intent.putExtra("air_quality", data.aqi)

                                        name_text.setText(data.name)
                                        aqi_value.setText(data.aqi)
                                        check_aqi(aqi_value, data.aqi.toIntOrNull())
                                        date(data.time, "yyyy-MM-dd'T'HH:mm:ssXXX")
                                    }
                                    true
                                }
                            }
                        }
                    }
                }


                override fun onFailure(call: Call<Response>, t: Throwable) {
                    Log.v("abcde", t.toString())
                    t.printStackTrace();
                }
            });
        }
        val btn_search_bar = view!!.findViewById(R.id.maps_tab_search_field_arrow) as ImageView

        btn_search_bar.setOnClickListener {
            map.clear()
            val text_search = view!!.findViewById(R.id.maps_tab_search_field) as EditText
            getAPISearch(text_search.getText().toString(), object : retrofit2.Callback<ResponseSearch> {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<ResponseSearch>, response: retrofit2.Response<ResponseSearch>) {
                    if(response.body()?.data.isNullOrEmpty()){
                        val dialogBuilder = AlertDialog.Builder(view?.context)
                        dialogBuilder.setTitle("Aucune station")

                            .setMessage("Aucune station n'existe. Veuillez faire une recherche valide")
                            .setNegativeButton("OK", DialogInterface.OnClickListener {
                                    dialog, id -> dialog.cancel()
                            })
                            .create()
                            .show()
                    }
                    else{
                        var count = 0
                        for(item in response.body()!!.data){
                            if(count < 20) {
                                count++
                                val marker = MarkerOptions()
                                    .position(LatLng(item.station.geo[0], item.station.geo[1]))
                                    .title(item.station.name)
                                    .icon(BitmapDescriptorFactory.fromBitmap(
                                        GoogleMapUtils.getBitmap(
                                            requireContext(),
                                            R.drawable.ic_map_marker)))

                                val mapMarker = map.addMarker(marker)
                                val data = StationData(item.uid,item.station.name,
                                    arrayOf(item.station.geo[0],item.station.geo[1]),item.aqi,item.time.stime + item.time.tz)
                                markersData[mapMarker] = data
                                map.setOnMarkerClickListener { marker ->
                                    val data = markersData[marker]
                                    if (data != null) {
                                        tab_station.visibility = View.VISIBLE
                                        val intent = Intent(context, DetailActivity::class.java)

                                        maps_tab_station.setOnClickListener{ startActivity(intent)}

                                        intent.putExtra("name", data.name)
                                        intent.putExtra("id", data.id.toString())
                                        intent.putExtra("air_quality", data.aqi)

                                        name_text.setText(data.name)
                                        aqi_value.setText(data.aqi)
                                        check_aqi(aqi_value, data.aqi.toIntOrNull())
                                        date(data.time, "yyyy-MM-dd HH:mm:ssXXX")
                                    }
                                    true
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                    Log.v("ccc",t.toString())
                    t.printStackTrace()
                }

            })
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun date(time: String?, format: String) {
        val formatter =  SimpleDateFormat(format)
        val timeFormatted = formatter.parse(time)
        //DateUtils.formatDateTime()
        val date_value = view!!.findViewById(R.id.maps_tab_station_date_value) as TextView
        date_value.setText(DateUtils.getRelativeTimeSpanString(timeFormatted.getTime()))
    }

    private fun check_aqi(aqiValue: TextView, aqi: Int?) {
        if(aqi != null) {
            if (0 <= aqi && aqi <= 50) {
                aqiValue.setTextColor(ContextCompat.getColor(context!!, R.color.aqi_good))
            } else if (51 <= aqi && aqi <= 100) {
                aqiValue.setTextColor(ContextCompat.getColor(context!!, R.color.aqi_moderate))
            } else if (101 <= aqi && aqi <= 150) {
                aqiValue.setTextColor(ContextCompat.getColor(context!!, R.color.aqi_unhealthy_sensitive))
            } else if (151 <= aqi && aqi <= 200) {
                aqiValue.setTextColor(ContextCompat.getColor(context!!, R.color.aqi_unhealthy))
            } else if (201 <= aqi && aqi <= 300) {
                aqiValue.setTextColor(ContextCompat.getColor(context!!, R.color.aqi_very_unhealthy))
            } else if (300 <= aqi) {
                aqiValue.setTextColor(ContextCompat.getColor(context!!, R.color.aqi_hazardous))
            }
        }
    }

}