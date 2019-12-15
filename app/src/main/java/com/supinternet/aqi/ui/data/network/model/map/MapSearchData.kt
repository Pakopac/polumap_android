package com.supinternet.aqi.data.network.model.map

data class MapSearchData(
    val aqi: String,
    val lat: Double,
    val lon: Double,
    val station: MapSearchStation,
    val uid: Int
)