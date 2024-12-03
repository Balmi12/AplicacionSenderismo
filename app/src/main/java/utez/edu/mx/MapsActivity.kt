package utez.edu.mx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import utez.edu.mx.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var mapTypeIndex = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar el boton de para cambiar el tipo de mapa
        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener {
            changeMapType()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.isBuildingsEnabled = true
        map.isTrafficEnabled = true

        val mexicoCtiy = LatLng(19.432608, -99.133209)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mexicoCtiy, 12f))

        map.addMarker(MarkerOptions().position(mexicoCtiy).title("Ciudad de Mexico"))

        val guadalajara = LatLng(20.659698, -103.349609)
        map.addMarker(
            MarkerOptions().position(guadalajara).title("Guadalajara").snippet("Capital de Jalisco")
        )

        val casa = LatLng(18.885226005642785, -99.12785702295126)
        map.addMarker(
            MarkerOptions().position(casa).title("Casa").snippet("Casa de Orlando")
        )
    }


    private fun changeMapType() {
        val mapTypes = listOf(
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_HYBRID
        )

        mapTypeIndex = (mapTypeIndex + 1) %mapTypes.size
        map.mapType = mapTypes[mapTypeIndex]
    }
}