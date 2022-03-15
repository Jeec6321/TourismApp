package com.android.tourismapp.Ui.Activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.android.tourismapp.Managers.ApplicationManager
import com.android.tourismapp.R
import com.android.tourismapp.ViewModels.PlacesViewModel
import com.android.tourismapp.databinding.ActivityMapsBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        placesViewModel = ViewModelProvider(this).get(PlacesViewModel::class.java)

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val bundle :Bundle ?=intent.extras
        val internet = bundle!!.getString("internetConnection")

        println("Messageeee: $internet")

        when(internet){
            "SUCCESS" -> {

            }

            "ERROR" -> {
                navController.navigateUp() // to clear previous navigation history
                navController.navigate(R.id.action_mapFragment_to_placesListFragment, bundle)
            }
        }

    }

    override fun onBackPressed() {
        if(ApplicationManager.isOnline(this)) {
            super.onBackPressed()
        }
    }
}