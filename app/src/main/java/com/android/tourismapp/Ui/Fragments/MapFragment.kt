package com.android.tourismapp.Ui.Fragments

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.android.tourismapp.Managers.ApplicationManager
import com.android.tourismapp.Managers.RetrofitManager
import com.android.tourismapp.Managers.SharedPreferencesManager
import com.android.tourismapp.R
import com.android.tourismapp.Ui.Elements.Button
import com.android.tourismapp.Ui.Elements.State
import com.android.tourismapp.ViewModels.PlacesViewModel
import com.android.tourismapp.databinding.FragmentMapBinding
import com.android.tourismapp.interfaces.Service
import com.android.tourismapp.models.GooglePlace
import com.android.tourismapp.models.apiResponses.ApiPlacesResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.*
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapBinding
    private lateinit var lastLocation: Task<Location>
    private lateinit var placesViewModel: PlacesViewModel

    private var latitude = 0.0
    private var longitude = 0.0
    private var radius: Int = 2000
    private lateinit var bSearch: Button
    private val service: Service = RetrofitManager.createService()
    private lateinit var favedPlaces: ArrayList<GooglePlace>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)

        placesViewModel = ViewModelProvider(requireActivity()).get(PlacesViewModel::class.java)

        Places.initialize(context, getString(R.string.google_maps_key))

        lastLocation = ApplicationManager.getLastLocation(requireActivity())

        ApplicationManager.getContinuousLocation(requireActivity()) {
            latitude = it.latitude
            longitude = it.longitude

            placesViewModel.setLatitude(it.latitude)
            placesViewModel.setLongitude(it.longitude)

            addMarkerAndRadius(latitude, longitude, getString(R.string.you_are_here), radius)
        }

        favedPlaces = SharedPreferencesManager().getFavPlaces(requireActivity())

        initViews()

        return binding.root
    }

    private fun initViews() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.sbRadius.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
                if (seekBar != null) {
                    binding.tvRadius.text = "${seekBar.progress} km"
                    radius = seekBar.progress * 1000
                    addMarkerAndRadius(latitude, longitude, getString(R.string.you_are_here), radius)

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        binding.bSearch.setOnClickListener {
            bSearch.setState(State.LOADING)

            if(latitude == 0.0 && longitude == 0.0){
                lastLocation.addOnSuccessListener {
                    if(it != null) {
                        latitude = it.latitude
                        longitude = it.longitude

                        placesViewModel.setLatitude(latitude)
                        placesViewModel.setLongitude(longitude)

                        addMarkerAndRadius(latitude, longitude, getString(R.string.you_are_here), radius)

                        callPlacesApi(latitude, longitude)
                    } else {
                        println("Error -> Location variable is null")
                    }
                }
            } else {
                callPlacesApi(latitude, longitude)
            }
        }

        bSearch = Button(binding.bSearchText, binding.pbSearchButton)
    }

    fun callPlacesApi(latitude: Double, longitude: Double){
        val locationQuery = "$latitude,$longitude"

        CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.IO){
                try{
                    var call = service.getPlaces(
                        "nearbysearch/json",
                        getString(R.string.google_maps_key),
                        locationQuery,
                        radius.toDouble(),
                        ""
                    )

                    if (call.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            val response = call.body()

                            response?.results?.sortBy { it.getDistancePlace(latitude, longitude) }

                            response?.results?.forEach {
                                println("${it.name} -> ${it.getDistancePlace(latitude, longitude)} km")
                                if (isPlaceInFavArray(it)) {
                                    it.faved = true
                                }
                            }

                            response?.results?.let { placesViewModel.setPlaces(it) }

                            bSearch.setState(State.ENABLE)

                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_mapFragment_to_placesListFragment)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        bSearch.setState(State.ENABLE)

                        var bundle = bundleOf(
                            "hidePlaces" to "true"
                        )

                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_mapFragment_to_placesListFragment, bundle)
                    }
                }
            }

        }

    }

    private fun isPlaceInFavArray(place1: GooglePlace): Boolean{
        favedPlaces.forEach {
            if(place1.name == it.name)
                return true
        }

        return false
    }

    private fun addMarkerAndRadius(latitude: Double, longitude: Double, title: String, radius: Int){
        val userMarker = LatLng(latitude, longitude)

        val marker = MarkerOptions().position(userMarker)

        val ratio = (Math.log(radius.toDouble()/1000)/Math.log(2.0))
        val zoom: Float = (14 - ratio).toFloat()

        mMap.clear()

        mMap.addMarker(marker.title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, zoom))

        if(radius != 0) {
            mMap.addCircle(
                CircleOptions().center(marker.position)
                    .radius(radius.toDouble())
                    .strokeWidth(3f)
                    .strokeColor(requireContext().getColor(R.color.soft_blue))
                    .fillColor(Color.argb(90,193,162,255))
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        lastLocation.addOnSuccessListener {
            if(it != null) {
                latitude = it.latitude
                longitude = it.longitude

                placesViewModel.setLatitude(latitude)
                placesViewModel.setLongitude(longitude)

                addMarkerAndRadius(latitude, longitude, getString(R.string.you_are_here), radius)
            } else {
                println("Error to take location")
            }
        }
    }

}