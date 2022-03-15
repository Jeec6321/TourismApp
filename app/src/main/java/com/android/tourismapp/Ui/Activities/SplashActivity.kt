package com.android.tourismapp.Ui.Activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.tourismapp.Managers.ApplicationManager
import com.android.tourismapp.Managers.RetrofitManager
import com.android.tourismapp.databinding.ActivitySplashBinding
import com.android.tourismapp.interfaces.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.android.tourismapp.R

//TODO: reintento cuando no tomca gps a la primera
//TODO: crash cuando se quita gps

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val service: Service = RetrofitManager.createService()
    private lateinit var locationListener: LocationListener
    private var sendPetition: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationListener = LocationListener {
            if(sendPetition) {
                callPlacesApi(it.latitude, it.longitude)
                sendPetition = false
            }
        }

        initViews()

        if(verifyPermissions()){
            startTimer()
        }
    }

    private fun startTimer(){
        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) { }

            override fun onFinish() {
                var internetConnection = "SUCCESS"

                if(!ApplicationManager.isOnline(this@SplashActivity)) {
                    internetConnection = "ERROR"
                }

                val i = Intent(baseContext, MainActivity::class.java).apply{
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("internetConnection", internetConnection)
                }

                startActivity(i)

                finish()
            }
        }.start()
    }

    private fun verifyPermissions(): Boolean {
        if(ApplicationManager.isLocationPermissionActivited(this)){
            if(ApplicationManager.isGpsEnabled(this)){
                return true
            } else {
                enableGpsDialog()
                return false
            }
        } else {
            ApplicationManager.requestPermission(this)
            return false
        }
    }

    private fun initViews(){
        binding.pbLoading.isVisible = false
    }

    private fun enableGpsDialog(){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setTitle(getString(R.string.gps_dialog))

            setMessage(getString(R.string.message_gps_dialog))

            setPositiveButton(R.string.positive_gps_dialog, DialogInterface.OnClickListener { dialogInterface, i ->
                ApplicationManager.launchGpsActivation(this@SplashActivity)
            })

            setNegativeButton(R.string.negative_gps_dialog, DialogInterface.OnClickListener { dialogInterface, i ->
                println("Negativo")
            })

            show()
        }
    }

    fun getLocation(){
        val taskLocation = ApplicationManager.getLastLocation(this)

        taskLocation.addOnSuccessListener {
            if(it != null){
                callPlacesApi(it.latitude, it.longitude)
            } else {
                Toast.makeText(this, "Error to take position", Toast.LENGTH_LONG).show()
                println("Error to take position")
            }
        }
    }

    fun callPlacesApi(latitude: Double, longitude: Double){
        val locationQuery = "$latitude,$longitude"

        CoroutineScope(Dispatchers.IO).launch {
            val call = service.getPlaces("json", getString(R.string.google_maps_key),locationQuery,2000.0,"")
            val places = call.body()
            if(call.isSuccessful) {
                val i = Intent(baseContext, MainActivity::class.java).apply{
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                startActivity(i)
            } else {
                println("ERROR!! :(")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == ApplicationManager.REQUEST_LOCATION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()

                if(verifyPermissions()){
                    startTimer()
                    //ApplicationManager.getContinuousLocation(this, locationListener)
                }

                binding.pbLoading.isVisible = true
                //getLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            ApplicationManager.REQUEST_GPS_CODE -> {
                if(ApplicationManager.isGpsEnabled(this)) {
                    Toast.makeText(this, "Gps is ok!", Toast.LENGTH_LONG).show()

                    if(verifyPermissions()){
                        startTimer()
                        //ApplicationManager.getContinuousLocation(this, locationListener)
                    }

                    binding.pbLoading.isVisible = true
                } else
                    Toast.makeText(this, "Gps is not enable", Toast.LENGTH_LONG).show()
            }
        }
    }
}