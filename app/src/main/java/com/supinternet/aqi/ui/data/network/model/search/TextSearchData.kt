package com.supinternet.aqi.data.network.model.search

data class TextSearchData(
    val aqi: String,
    val station: TextSearchStation,
    val time: TextSearchTime,
    val uid: Int
)