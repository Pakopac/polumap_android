package com.supinternet.aqi.ui.screens.main.tabs.favs

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.model.station.Data
import com.supinternet.aqi.ui.screens.main.tabs.aroundme.MapsTab
import kotlinx.android.synthetic.main.fragment_favs.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Fav(val stationName : String,  val stationId: Int)
class FavsTab : Fragment() {

    data class GetFavs(val data: Data)

    companion object {
        private val ARG_USERID = "user_id"

        fun newInstance(user_id: String):FavsTab{
            val args: Bundle = Bundle()
            args.putSerializable(ARG_USERID, user_id)
            val fragment = FavsTab()
            fragment.arguments = args
            return fragment
        }
        interface API {
            @GET("getFavs")

            fun getDataById(@Query("userID") id: String): Call<List<GetFavs>>
        }

        val api = Retrofit.Builder()
            .baseUrl("https://us-central1-polumapproject.cloudfunctions.net/api/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(API::class.java)

        fun getAPI(id: String, callback: Callback<List<GetFavs>>) {
            return api.getDataById(id).enqueue(callback)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getAPI(getArguments()!!.getString("user_id"), object : retrofit2.Callback<List<GetFavs>> {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFailure(call: Call<List<GetFavs>>, t: Throwable) {
                Log.v("error", t.message)
            }

            override fun onResponse(
                call: Call<List<GetFavs>>, response: retrofit2.Response<List<GetFavs>>
            ) {
                val arrrayResult = mutableListOf<Data>()
                var data = response.body()
                Log.v("favs",data.toString())

                GlobalScope.launch(Dispatchers.Default) {
                    for (station in data!!) {
                        Log.v("favs1", station.data.toString())
                        arrrayResult.add(station.data)
                    }

                    withContext(Dispatchers.Main) {
                        lists.apply {
                            layoutManager = LinearLayoutManager(activity)
                            adapter = ListAdapter(arrrayResult)
                            waitingBar?.visibility = View.GONE
                        }
                    }
                }

            }

        })
        super.onViewCreated(view, savedInstanceState)

        var data = arrayOf(Fav("Paris",5722), Fav("Lyon",3028), Fav("Toulouse",3836))

        GlobalScope.launch(Dispatchers.Default) {


        }
    }
}