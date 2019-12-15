package com.supinternet.aqi.data.network.model.history

import java.util.*


data class History (
    val time : Date,
    val idx : Int,
    val data : HistoryData
)
data class HistoryData (
    val co : Value,
    val no2 : Value,
    val o3 : Value,
    val p : Value,
    val pm10 : Value,
    val pm25 : Value,
    val so2 : Value,
    val t : Value,
    val w : Value
)
data class Value(val v: Number)
