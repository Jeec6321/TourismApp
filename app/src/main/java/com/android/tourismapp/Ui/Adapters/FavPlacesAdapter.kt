package com.android.tourismapp.Ui.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.tourismapp.Managers.ApplicationManager
import com.android.tourismapp.models.GooglePlace
import com.android.tourismapp.R

class FavPlacesAdapter(
    val listener: Listener,
    val places: ArrayList<GooglePlace>,
    val latitude: Double,
    val longitude: Double): RecyclerView.Adapter<FavPlacesAdapter.FavPlaceViewHolder>() {

    class FavPlaceViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val tvPlaceName = view.findViewById<TextView>(R.id.tv_place_name)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvDistance = view.findViewById<TextView>(R.id.tv_distance)
        val ivFavIcon = view.findViewById<ImageView>(R.id.iv_make_favorite)
        val ivDelete = view.findViewById<ImageView>(R.id.iv_delete)
        val clBackground = view.findViewById<ConstraintLayout>(R.id.cl_background)

        fun renderPlace(place: GooglePlace, listener: Listener, latitude: Double, longitude: Double) {
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

            if (latitude != 0.0 && longitude != 0.0) {
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

            ivDelete.setOnClickListener {
                listener.action(PlaceActions.UN_FAVED, place)
            }

            clBackground.setOnClickListener {
                if(ApplicationManager.isOnline(view.context)) {
                    listener.action(PlaceActions.SHOW_ONLINE_DETAILS, place)

                    return@setOnClickListener
                }

                if(place.details != null) {
                    listener.action(PlaceActions.SHOW_OFFLINE_DETAILS, place)
                } else {
                    Toast.makeText(view.context, view.context.getString(R.string.check_internet), Toast.LENGTH_LONG).show()

                    println("place.details null!!")
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavPlaceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return FavPlaceViewHolder(layoutInflater.inflate(R.layout.row_favorite_place, parent, false))
    }

    override fun onBindViewHolder(holder: FavPlaceViewHolder, position: Int) {
        holder.renderPlace(places[position], listener, latitude, longitude)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    interface Listener {
        fun action(action: PlaceActions, place: GooglePlace)
    }

    enum class PlaceActions {
        FAVED,
        UN_FAVED,
        SHOW_OFFLINE_DETAILS,
        SHOW_ONLINE_DETAILS
    }

}