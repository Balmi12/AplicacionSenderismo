package utez.edu.mx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.newSingleThreadContext
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var gravity: FloatArray? = null
    private var magnetic: FloatArray? = null

    private lateinit var tvAltitude: TextView
    private lateinit var tvOrientation: TextView
    private lateinit var tvPosition: TextView
    private lateinit var compassView: ImageView
    private lateinit var btnMostrarMapa: Button
    private lateinit var btnSaveData: Button
    private lateinit var btnCerrarSesion: Button

    private var email: String? = null
    private var altitude: Double? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var database: DatabaseReference  // Firebase reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        tvAltitude = findViewById(R.id.tv_altitude)
        tvOrientation = findViewById(R.id.tv_orientation)
        tvPosition = findViewById(R.id.tv_position)
        compassView = findViewById(R.id.compassView)
        btnMostrarMapa = findViewById(R.id.btn_mostrar_mapa)
        btnSaveData = findViewById(R.id.btn_save_data) // Inicialización del botón de mostrar cambios
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        val cosa = intent.getStringExtra("email")
        email = cosa

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Gestión de sensores
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        checkPermissions()

        // Registramos sensores
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gravitySensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magneticSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }

        // Acción al presionar el botón "Guardar Datos"
        btnSaveData.setOnClickListener {
            saveDataToFirebase()  // Guardar datos en Firebase
        }


        // Acción al presionar el botón "Mostrar Mapa"
        btnMostrarMapa.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("email", cosa)
            startActivity(intent)
        }


        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getAltitude()
        }
    }

    private fun getAltitude() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    altitude = location.altitude
                    latitude = location.latitude
                    longitude = location.longitude
                    tvAltitude.text = "Altitud: ${altitude?.roundToInt()} m"
                    tvPosition.text = "Posición: Lat: $latitude, Lon: $longitude"
                } else {
                    tvPosition.text = "Posición: Sin datos de ubicación"
                }
            }.addOnFailureListener {
                tvPosition.text = "Error al obtener la ubicación"
            }
        }
    }

    // Método para guardar los datos en Firebase
    private fun saveDataToFirebase() {
        if (altitude != null && latitude != null && longitude != null) {
            val data = HashMap<String, Any>()
            data["email"] = email ?: ""
            data["altitud"] = altitude?.roundToInt() ?: 0
            data["latitud"] = latitude ?: 0.0
            data["longitud"] = longitude ?: 0.0
            data["orientación"] = tvOrientation.text.toString()

            // Guardar los datos en Firebase bajo una clave única
            val newData = database.child("datos").push()  // "datos" es el nodo donde se almacenarán
            newData.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos guardados correctamente en Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar los datos en Firebase", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No hay datos disponibles para guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getAltitude()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_GRAVITY -> {
                    gravity = it.values
                    updateOrientation()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magnetic = it.values
                    updateOrientation()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateOrientation() {
        if (gravity != null && magnetic != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            if (SensorManager.getRotationMatrix(R, I, gravity, magnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)

                val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                tvOrientation.text = "Orientación: ${azimuth.roundToInt()}°"

                var adjustedAzimuth = azimuth % 360
                if (adjustedAzimuth < 0) adjustedAzimuth += 360
                val calibratedAzimuth = (adjustedAzimuth + 270) % 360
                compassView.rotation = -calibratedAzimuth
            }
        }
    }
}
