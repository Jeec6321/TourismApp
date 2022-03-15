package com.android.tourismapp.Ui.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.tourismapp.models.GooglePlace
import com.android.tourismapp.R

class PlacesAdapter(
    val listener: PlaceListener,
    val places: ArrayList<GooglePlace>,
    val latitude: Double,
    val longitude: Double): RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val tvPlaceName = view.findViewById<TextView>(R.id.tv_place_name)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvDistance = view.findViewById<TextView>(R.id.tv_distance)
        val ivFavIcon = view.findViewById<ImageView>(R.id.iv_make_favorite)
        val clBackground = view.findViewById<ConstraintLayout>(R.id.cl_background)

        fun renderPlace(place: GooglePlace, listener: PlaceListener, latitude: Double, longitude: Double) {
            tvPlaceName.text = place.name

            if(place.openingHours != null) {
                tvStatus.text =
                if (place.openingHours.openNow) {
                    tvStatus.setTextColor(view.resources.getColor(R.color.green))
                    view.resources.getString(R.string.is_open)
                } else {
                    tvStatus.setTextColor(view.resources.getColor(R.color.primary_dark_color))
                    view.resources.getString(R.string.is_close)
                }
            } else {
                tvStatus.visibility = View.GONE
            }

            if(latitude != 0.0 && longitude != 0.0) {
                var distance = place.getDistancePlace(latitude, longitude)

                var resource: Int = R.string.km_distance_text

                if (distance < 1) {
                    resource = R.string.m_distance_text
                    distance *= 1000.0.toFloat()
                }

                tvDistance.text =
                    view.resources.getString(resource, String.format("%.2f", distance))
            } else {
                tvDistance.isVisible = false
            }

            ivFavIcon.setImageResource(if(place.faved) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

            ivFavIcon.setOnClickListener {
                if(place.faved) {
                    listener.action(PlaceActions.UN_FAVED, place)
                    //ivFavIcon.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    listener.action(PlaceActions.FAVED, place)
                    //ivFavIcon.setImageResource(R.drawable.ic_favorite)
                }
            }

            clBackground.setOnClickListener {
                listener.action(PlaceActions.SHOW_DETAILS, place)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return PlaceViewHolder(layoutInflater.inflate(R.layout.row_place, parent, false))
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.renderPlace(places[position], listener, latitude, longitude)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    interface PlaceListener {
        fun action(action: PlaceActions, place: GooglePlace)
    }

    enum class PlaceActions {
        FAVED,
        UN_FAVED,
        SHOW_DETAILS
    }

}