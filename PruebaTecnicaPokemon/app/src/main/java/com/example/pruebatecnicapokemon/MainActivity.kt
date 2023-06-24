package com.example.pruebatecnicapokemon

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.os.Vibrator


class MainActivity : AppCompatActivity(), LocationListener{

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var totalDistance: Float = 0f
    private var initialLocation: Location? = null
    private var vibrator: Vibrator? = null
    private var posicionInicial: Location? = null



    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var initialLocation: Location? = null
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val backButton = findViewById<Button>(R.id.button)
        backButton.setOnClickListener {
            val intent = Intent(this, PokemonInfo::class.java)
            startActivity(intent)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)


        enableLocation()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun isLocationPermission()= ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        //if(!::map.isInitialized) return
        if(isLocationPermission()){
            startLocationUpdates()
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Habilita los permisos en los ajustes", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(this, "Habilita los permisos en los ajustes", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(isLocationPermission()){
            startLocationUpdates()
        }else{
            Toast.makeText(this, "Habilita los permisos en los ajustes", Toast.LENGTH_SHORT).show()
        }
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                initialLocation = location
            }
        }

    }

    override fun onLocationChanged(location: Location) {
        if (posicionInicial == null) {
            // Establece la posición inicial
            posicionInicial = location
        } else {
            // Calcula la distancia recorrida en metros desde la posición inicial
            val distanciaRecorrida = location.distanceTo(posicionInicial!!)

            // Verifica si se ha recorrido la distancia requerida (10 metros en este caso)
            if (distanciaRecorrida >= 10) {
                // Detén la actualización de ubicación para evitar vibraciones repetidas
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager.removeUpdates(this)

                // Activa la vibración
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(3, VibrationEffect.DEFAULT_AMPLITUDE))
                }else{
                    vibrator.vibrate(3)
                }

                // Cambia a la actividad PokemonInfo
                val intent = Intent(this, PokemonInfo::class.java)
                startActivity(intent)
            }
        }
    }


}


