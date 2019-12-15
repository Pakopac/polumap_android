package com.supinternet.aqi.data.network.model.ranking

import com.supinternet.aqi.data.network.model.ranking.City

data class Ranking(
    val cities: List<City>,
    val time: String,
    val version: Int
)