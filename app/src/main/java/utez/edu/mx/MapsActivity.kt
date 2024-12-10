package utez.edu.mx

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import utez.edu.mx.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference

    private var mapTypeIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance().getReference("datos")

        // Obtener el SupportMapFragment y notificar cuando el mapa esté listo
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar el botón para cambiar el tipo de mapa
        findViewById<Button>(R.id.btnChangeMapType).setOnClickListener {
            changeMapType()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()

        map.uiSettings.isZoomControlsEnabled = true
        map.isBuildingsEnabled = true
        map.isTrafficEnabled = true

        // Cargar marcadores desde Firebase
        loadMarkersFromFirebase()
    }

    private fun enableLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permiso otorgado, habilitar ubicación
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    map.addMarker(MarkerOptions().position(currentLatLng).title("Tu ubicación"))
                }
            }
        }
    }

    private fun changeMapType() {
        val mapTypes = listOf(
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_HYBRID
        )

        mapTypeIndex = (mapTypeIndex + 1) % mapTypes.size
        map.mapType = mapTypes[mapTypeIndex]
    }

    private fun loadMarkersFromFirebase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cosa = intent.getStringExtra("email")

                for (locationSnapshot in snapshot.children) {
                    // Leer los valores de la estructura
                    val email = locationSnapshot.child("email").getValue(String::class.java)
                    val altitude = locationSnapshot.child("altitud").getValue(Int::class.java)
                    val latitude = locationSnapshot.child("latitud").getValue(Double::class.java)
                    val longitude = locationSnapshot.child("longitud").getValue(Double::class.java)
                    val orientation = locationSnapshot.child("orientación").getValue(String::class.java)

                    // Validar y añadir el marcador
                    if (latitude != null && longitude != null && email == cosa) {
                        val position = LatLng(latitude, longitude)
                        val markerTitle = "Altitud: $altitude, Orientación: $orientation"
                        map.addMarker(MarkerOptions().position(position).title(markerTitle))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                error.toException().printStackTrace()
            }
        })
    }
}
