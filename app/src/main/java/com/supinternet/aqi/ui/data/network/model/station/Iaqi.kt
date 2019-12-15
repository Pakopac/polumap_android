package com.supinternet.aqi.data.network.model.station

data class Iaqi(
    val co: IaqiVal,
    val no2: IaqiVal,
    val o3: IaqiVal,
    val p: IaqiVal,
    val pm10: IaqiVal,
    val pm25: IaqiVal,
    val so2: IaqiVal,
    val t: IaqiVal
)