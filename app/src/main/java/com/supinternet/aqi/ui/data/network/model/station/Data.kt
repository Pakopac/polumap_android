package com.supinternet.aqi.data.network.model.station

data class Data(
    val aqi: Int,
    val attributions: List<Attribution>,
    val city: City,
    val dominentpol: String,
    val iaqi: Iaqi,
    val idx: Int,
    val time: Time
)