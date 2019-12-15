package com.supinternet.aqi.ui.screens.main.tabs.travel

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.model.ranking.City
import kotlinx.android.synthetic.main.travel_tab_item_cell.view.*

class CityAdapter(var cities: List<City>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CityItemCell(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.travel_tab_item_cell, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as CityItemCell).bindCity(cities[position], position, context)
    }

    fun updateValues(cities: List<City>) {
        this.cities = cities
    }
}

class CityItemCell(v: View) : RecyclerView.ViewHolder(v) {
    //private val flag: ImageView = v.country_flag
    private val cityPosition: TextView = v.city_position
    private val name: TextView = v.city_name
    private val flag: ImageView = v.city_flag
    private val pollution: TextView = v.pollution_value

    fun bindCity(city: City, position: Int, context: Context) {
        val country = try {
            context.getString(
                context.resources.getIdentifier(
                    "country_${city.country}_name",
                    "string",
                    context.packageName
                )
            )
        } catch (e: Exception) {
            city.country
        }
        val flagRes: Int = try {
            context.resources.getIdentifier(
                "ic_flag_country_${city.country.toLowerCase()}",
                "drawable",
                context.packageName
            )
        } catch (e: Exception) {
            R.drawable.ic_flag_country_fr
        }

        name.setCityAndCountry(city.city, country)
        cityPosition.text = (position + 1).toString()

        flag.setImageResource(flagRes)
        pollution.text = city.station.a
    }
}

fun TextView.setCityAndCountry(city: String, country: String) {
    text = SpannableString("$city: $country").apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, city.length + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
}