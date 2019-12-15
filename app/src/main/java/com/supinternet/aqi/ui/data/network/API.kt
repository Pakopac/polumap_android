package com.supinternet.aqi.data.network

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.supinternet.aqi.data.network.model.history.History
import com.supinternet.aqi.data.network.model.map.MapSearch
import com.supinternet.aqi.data.network.model.ranking.Ranking
import com.supinternet.aqi.data.network.model.search.TextSearch
import com.supinternet.aqi.data.network.model.station.StationData
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.logging.HttpLoggingInterceptor



interface AQIAPI {

    @GET("map/bounds/")
    fun searchInMapAsync(
        @Query(
            "latlng",
            encoded = false
        ) latLng: String, @Query("token") token: String
    ): Deferred<MapSearch>

    @GET("search/")
    fun textSearchAsync(@Query("keyword") search: String, @Query("token") token: String): Deferred<TextSearch>

    @GET("feed/@{stationId}/")
    fun currentStationData(@Path("stationId") stationId: Int, @Query("token") token: String): Deferred<StationData>

    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        private const val BASE_URL: String = "https://api.waqi.info/"

        @Synchronized
        fun getInstance(): AQIAPI {
            retrofit = retrofit ?: buildRetrofit()
            return retrofit!!.create(AQIAPI::class.java)
        }

        private fun buildRetrofit(): Retrofit {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }
    }

}

interface RankingAPI {

    @GET("ranking/index.json")
    fun getRanking(): Deferred<Ranking>

    companion object Factory {
        @Volatile
        private var retrofit: Retrofit? = null

        private const val BASE_URL: String = "https://waqi.info/rtdata/"

        @Synchronized
        fun getInstance(): RankingAPI {
            retrofit = retrofit ?: buildRetrofit()
            return retrofit!!.create(RankingAPI::class.java)
        }

        private fun buildRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        }

        interface HistoryApi {

            @GET("api/getStationHistory?id=5722")
            fun getHistory() :  Deferred<List<History>>

            companion object Factory {
                @Volatile
                private var retrofit: Retrofit? = null

                private const val BASE_URL: String = "https://us-central1-polumapproject.cloudfunctions.net/"

                @Synchronized
                fun getInstance(): HistoryApi {
                    retrofit = retrofit ?: buildRetrofit()
                    return retrofit!!.create(HistoryApi::class.java)
                }

                private fun buildRetrofit(): Retrofit {
                    val logging = HttpLoggingInterceptor()
                    logging.level = (HttpLoggingInterceptor.Level.BODY)
                    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

                    val client = OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build()

                    return Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .build()
                }
            }
        }
    }}
