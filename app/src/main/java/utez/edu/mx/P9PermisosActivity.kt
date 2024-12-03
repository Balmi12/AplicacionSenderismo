package utez.edu.mx

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class P9PermisosActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var tvCoordenadas: TextView

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p9_permisos)

        val btnObtenerUbicacion: Button = findViewById(R.id.btn_obtener_ubicacion)
        tvCoordenadas = findViewById(R.id.tv_coordenadas)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnObtenerUbicacion.setOnClickListener {
            obtenerUbicacion()
        }
    }

    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null){
                        val latitud = location.latitude
                        val longitud = location.longitude
                        tvCoordenadas.text = "coordenadas : $latitud , $longitud"
                    }else{
                        Toast.makeText(this, "no e pudo obtener la ubicacion", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicacion denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}