package com.android.tourismapp.Ui.Fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tourismapp.Managers.ApplicationManager
import com.android.tourismapp.Managers.RetrofitManager
import com.android.tourismapp.Managers.SharedPreferencesManager
import com.android.tourismapp.Ui.Adapters.FavPlacesAdapter
import com.android.tourismapp.Ui.Adapters.PlacesAdapter
import com.android.tourismapp.ViewModels.PlacesViewModel
import com.android.tourismapp.databinding.FragmentPlacesListBinding
import com.android.tourismapp.models.GooglePlace
import com.google.gson.Gson
import com.android.tourismapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesListFragment : Fragment(), PlacesAdapter.PlaceListener, FavPlacesAdapter.Listener {

    private lateinit var binding: FragmentPlacesListBinding
    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var favPlacesAdapter: FavPlacesAdapter
    private var currentView: ViewPlaces = ViewPlaces.PLACES
    private val servide = RetrofitManager.createService()
    private var hidePlaces: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.getString("hidePlaces") == "true"){
                hidePlaces = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlacesListBinding.inflate(layoutInflater)

        placesViewModel = ViewModelProvider(requireActivity()).get(PlacesViewModel::class.java)

        initViews()

        placesViewModel.places.observe(viewLifecycleOwner, Observer {
            if(placesAdapter != null)
                placesAdapter.notifyDataSetChanged()

            if(currentView == ViewPlaces.FAV_PLACES){
                showFavoritePlaces()
            }
        })

        return binding.root
    }

    private fun initViews(){
        binding.rvPlaces.layoutManager = LinearLayoutManager(context)

        placesAdapter = PlacesAdapter(
            this,
            placesViewModel.places.value!!,
            placesViewModel.latitude.value!!,
            placesViewModel.longitude.value!!
        )

        setPlacesAdapter()

        binding.clFavorites.setOnClickListener {
            showFavoritePlaces()
        }

        binding.clPlaces.setOnClickListener {
            binding.tvTitle.text = getString(R.string.places)
            setPlacesAdapter()
        }

        binding.tvTitle.setOnClickListener {
            //Clear data saved
            //SharedPreferencesManager().setFavPlaces(requireActivity(), ArrayList())
            //SharedPreferencesManager().setPlaces(requireActivity(), ArrayList())
        }

        binding.ivBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_placesListFragment_to_mapFragment)
        }

        handleConectionState()
    }

    private fun handleConectionState(){
        if(!ApplicationManager.isOnline(requireContext()) || hidePlaces){
            internetErrorDialog()

            binding.clPlaces.isVisible = false

            binding.clFavorites.isVisible = false

            binding.ivBack.isVisible = false

            showFavoritePlaces()
        }
    }

    private fun internetErrorDialog(){
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle(getString(R.string.internet_error_dialog_title))

            setMessage(getString(R.string.internet_error_dialog_message))

            setNeutralButton(R.string.accept, DialogInterface.OnClickListener { dialogInterface, i ->

            })

            show()
        }
    }

    private fun showFavoritePlaces(){
        currentView = ViewPlaces.FAV_PLACES

        binding.tvTitle.text = getString(R.string.favorites)

        favPlacesAdapter = FavPlacesAdapter(
            this,
            SharedPreferencesManager().getFavPlaces(requireActivity()),
            placesViewModel.latitude.value!!,
            placesViewModel.longitude.value!!
        )

        setFavPlacesAdapter()
    }

    private fun setPlacesAdapter() {
        currentView = ViewPlaces.PLACES
        binding.rvPlaces.adapter = placesAdapter
    }

    private fun setFavPlacesAdapter() {
        binding.rvPlaces.adapter = favPlacesAdapter
    }

    override fun action(action: PlacesAdapter.PlaceActions, place: GooglePlace) {
        when(action) {
            PlacesAdapter.PlaceActions.FAVED -> {
                placesViewModel.addFavPlace(requireActivity(), place)
            }

            PlacesAdapter.PlaceActions.UN_FAVED -> {
                placesViewModel.removeFavPlace(requireActivity(), place)
            }

            PlacesAdapter.PlaceActions.SHOW_DETAILS -> {
                callPlaceDetials(place)
            }
        }
    }

    fun callPlaceDetials(place: GooglePlace) {

        CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.IO){
                try{
                    val call = servide.getPlaceDestails("details/json", getString(R.string.google_maps_key), place.placeId)

                    if(call.isSuccessful) {
                        if(place.faved){
                            place.details = call.body()?.result!!
                            placesViewModel.putPlaceDetails(requireActivity(), place)
                        }

                        val placeString = Gson().toJson(call.body()?.result)

                        println("PlaceString: $placeString")

                        var bundle = bundleOf(
                            "placeDetails" to placeString,
                            "latitude" to "${placesViewModel.latitude.value}",
                            "longitude" to "${placesViewModel.longitude.value}"
                        )

                        withContext(Dispatchers.Main) {
                            Navigation.findNavController(binding.root).navigate(R.id.action_placesListFragment_to_placeDetails, bundle)
                        }

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        hidePlaces = true
                        handleConectionState()
                    }
                }
            }
        }
    }

    override fun action(action: FavPlacesAdapter.PlaceActions, place: GooglePlace) {
        when(action) {
            FavPlacesAdapter.PlaceActions.UN_FAVED -> {
                placesViewModel.removeFavPlace(requireActivity(), place)
            }

            FavPlacesAdapter.PlaceActions.SHOW_OFFLINE_DETAILS -> {
                var bundle = bundleOf(
                    "placeDetails" to Gson().toJson(place.details),
                    "latitude" to "${placesViewModel.latitude.value}",
                    "longitude" to "${placesViewModel.longitude.value}"
                )

                Navigation.findNavController(binding.root).navigate(R.id.action_placesListFragment_to_placeDetails, bundle)
            }

            FavPlacesAdapter.PlaceActions.SHOW_ONLINE_DETAILS -> {
                callPlaceDetials(place)
            }
        }
    }
}

enum class ViewPlaces {
    PLACES,
    FAV_PLACES
}