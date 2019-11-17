package com.supinternet.aqi.ui.screens.main.tabs.aroundme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.supinternet.aqi.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class MapsTab : Fragment(), OnMapReadyCallback {

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

        (childFragmentManager.findFragmentById(R.id.maps_tab_gmap) as SupportMapFragment).getMapAsync(
            this
        )
    }

    data class Response(
        val data: List<GetData>
    )
    data class GetData(
        val lat: Float,
        val lon: Float,
        val uid: Float,
        val aqi: String,
        val station: Map<String, String>?
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(), R.raw.map_style
            )
        )

        val btn_search = view!!.findViewById(R.id.maps_tab_search_zone_button) as MaterialButton

        btn_search.setOnClickListener {
            getAPI("39.379436,116.091230,40.235643,116.784382", object : retrofit2.Callback<Response> {

                override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                    val GetDatas = response.body()
                    Log.v("aaaaa", response.toString())
                    Log.v("abcde", GetDatas.toString())
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {

                    Log.v("abcde", t.toString())
                    t.printStackTrace();
                }
            });
        }
    }

}