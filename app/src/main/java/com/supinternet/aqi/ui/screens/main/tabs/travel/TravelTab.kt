package com.supinternet.aqi.ui.screens.main.tabs.travel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.supinternet.aqi.R
import com.supinternet.aqi.data.network.RankingAPI
import com.supinternet.aqi.data.network.model.ranking.City
import kotlinx.android.synthetic.main.fragment_maps_search_card.*
import kotlinx.android.synthetic.main.travel_tab_recycler_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TravelTab : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.travel_tab_recycler_view, container, false)
    }

    var cities: List<City> = listOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var self = this

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val rankings = withContext(Dispatchers.IO) {
                    RankingAPI.getInstance().getRanking().await()
                }
                self.cities = rankings.cities
                travel_tab_recycler_view.layoutManager = LinearLayoutManager(requireContext())
                travel_tab_recycler_view.adapter = CityAdapter(cities, requireContext())
            } catch (e: Exception) {
                print(e)
            }
        }

        maps_tab_search_field.doOnTextChanged { text, start, count, after ->
            val query = text.toString()
            val filter: (List<City>) -> List<City> = { l: List<City> ->
                l.filter {
                    it.city.toLowerCase().contains(query.toLowerCase())
                }
            }
            this.updateCities(filter)
        }
    }

    fun updateCities(filter: (List<City>) -> List<City>) {
        val sorted = this.cities.sortedByDescending {
            it.station.a.toInt()
        }
        val cities = filter(sorted)
        if (cities.isEmpty()) {
            empty_view_text.visibility = View.VISIBLE
            travel_tab_recycler_view.visibility = View.GONE
        } else {
            empty_view_text.visibility = View.GONE
            travel_tab_recycler_view.visibility = View.VISIBLE
        }

        (travel_tab_recycler_view.adapter as CityAdapter).updateValues(cities)
        (travel_tab_recycler_view.adapter as CityAdapter).notifyDataSetChanged()
    }
}
