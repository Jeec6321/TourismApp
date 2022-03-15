package com.android.tourismapp.Ui.Fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tourismapp.R
import com.android.tourismapp.Ui.Adapters.ReviewAdapter
import com.android.tourismapp.databinding.FragmentPlaceDetailsBinding
import com.android.tourismapp.models.GooglePlace
import com.android.tourismapp.models.apiResponses.PlaceDetail
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception

private const val PLACE = "placeDetails"
private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"

class PlaceDetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentPlaceDetailsBinding
    private lateinit var mMap: GoogleMap
    private var placeString: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var placeDetail: PlaceDetail
    private lateinit var reviewsAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeString = it.getString(PLACE)
            latitude = it.getString(LATITUDE)?.toDouble() ?: 0.0
            longitude = it.getString(LONGITUDE)?.toDouble() ?: 0.0
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceDetailsBinding.inflate(layoutInflater)

        initViews()

        try {
            val type = object : TypeToken<PlaceDetail>() {}.type

            placeDetail = Gson().fromJson(placeString, type)

            println("Recibido: ${Gson().toJson(placeDetail)}")

            renderView()
        } catch (e: Exception){
            println("Exception placeDetail exc: ${e.toString()}")
            Toast.makeText(requireActivity(), "Error to load place", Toast.LENGTH_LONG).show()
        }

        Places.initialize(requireContext(), getString(R.string.google_maps_key))

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    private fun initViews() {
        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_placeDetails_to_placesListFragment)
        }
    }

    private fun renderView() {
        binding.rvReviews.layoutManager = LinearLayoutManager(context)

        if(placeDetail.reviews != null){
            if(placeDetail.reviews!!.size != 0) {
                reviewsAdapter = ReviewAdapter(placeDetail.reviews!!)
                binding.rvReviews.adapter = reviewsAdapter
            } else {
                binding.tvWarningReviews.isVisible = true
            }
        } else {
            binding.tvWarningReviews.isVisible = true
        }

        binding.tvPlaceName.text = placeDetail.name

        if(placeDetail.formattedAddress != null)
            binding.tvAddress.text = placeDetail.formattedAddress
        else
            binding.clAddress.isVisible = false

        if(placeDetail.formattedPhoneNumber != null)
            binding.tvPhone.text = placeDetail.formattedPhoneNumber
        else
            binding.clPhone.isVisible = false
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String){
        val userMarker = LatLng(latitude, longitude)

        val marker = MarkerOptions().position(userMarker)

        //mMap.clear()

        mMap.addMarker(marker.title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, getCamaraZoom()))
    }

    fun getCamaraZoom(): Float{
        var radius = 2000.0

        if(latitude != 0.0 && longitude != 0.0) {
            var result = FloatArray(1)

            android.location.Location.distanceBetween(
                latitude, longitude,
                placeDetail.geometry.location.lat, placeDetail.geometry.location.lng,
                result
            )

            radius = result.get(0).toDouble()
        }

        radius *= 1.3

        val ratio = (Math.log(radius/1000)/Math.log(2.0))
        val zoom: Float = (14 - ratio).toFloat()

        return zoom
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaceDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(PLACE, param1)
                    putString(LATITUDE, param2)
                    putString(LONGITUDE, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        placeDetail.geometry.location.let {
            addMarker(it.lat, it.lng, getString(R.string.place))
        }

        if(latitude != 0.0 && longitude != 0.0){
            addMarker(latitude, longitude, getString(R.string.you_are_here))
        }
    }
}