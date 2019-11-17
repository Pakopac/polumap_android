package com.supinternet.aqi.ui.screens.main.tabs.aroundme

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.supinternet.aqi.ui.utils.GoogleMapUtils
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
        val lat: Double,
        val lon: Double,
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
            val coordinate:String =
                map.getProjection().getVisibleRegion().farLeft.latitude.toString() + ',' +
                        map.getProjection().getVisibleRegion().farLeft.longitude.toString() + ',' +
                        map.getProjection().getVisibleRegion().nearRight.latitude.toString() + ',' +
                        map.getProjection().getVisibleRegion().nearRight.longitude.toString()

            getAPI(coordinate, object : retrofit2.Callback<Response> {

                override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                    map.clear()
                    var count = 0
                    val GetDatas = response.body()
                    Log.v("aaaaa", response.body().toString())
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
                        for(item in response.body()!!.data){
                            if(count < 20) {
                                count++
                                map.addMarker(MarkerOptions()
                                    .position(LatLng(item.lat, item.lon))
                                    .title(item.station?.get("name"))
                                    .icon(BitmapDescriptorFactory.fromBitmap(
                                        GoogleMapUtils.getBitmap(
                                            requireContext(),
                                            R.drawable.ic_map_marker)))
                                )
                            }
                        }
                    }
                    //map.addMarker(MarkerOptions().position(LatLng(47.055817, 1.042124 )).title("Verneuil, France"))

                    //map.addMarker(MarkerOptions().position(LatLng(44.92545, 1.8457342)).title("Marker1"))
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {

                    Log.v("abcde", t.toString())
                    t.printStackTrace();
                }
            });
        }
    }

}