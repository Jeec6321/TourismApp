package com.android.tourismapp.Managers

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class ApplicationManager: Application() {

    companion object {
        const val REQUEST_LOCATION_CODE = 11011
        const val REQUEST_GPS_CODE = 11111

        fun launchGpsActivation(activity: Activity): Boolean {
            if(!isGpsEnabled(activity)) {
                val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

                Toast.makeText(activity, "Please active the gps to use the app", Toast.LENGTH_LONG).show()

                activity.startActivityForResult(intent1, REQUEST_GPS_CODE)
                return false
            }

            return true
        }

        fun isGpsEnabled(activity: Activity): Boolean {
            val locationManager: LocationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        fun getContinuousLocation(activity: Activity, locationListener: LocationListener) {
            val locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission(activity)
            } else {
                if(isGpsEnabled(activity)){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, locationListener)
                } else {
                    Toast.makeText(activity, "Error gps", Toast.LENGTH_LONG).show()
                }
            }
       }

        fun getLastLocation(activity: Activity): Task<Location> {
            var locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission(activity)
            }

            return locationClient.lastLocation
        }

        fun isLocationPermissionActivited(activity: Activity) = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        println("Internet NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        println("Internet NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        println("Internet NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }

        fun requestPermission(activity: Activity) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(activity, "Please active the location permission from the device settings", Toast.LENGTH_LONG).show()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
            }
        }
    }

}