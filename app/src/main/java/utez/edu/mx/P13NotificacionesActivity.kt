package utez.edu.mx

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class P13NotificacionesActivity : AppCompatActivity() {
    private val channelId = "test_id"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p13_notificaciones)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName  = "Test Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para notificaciones de prueba"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d("Depuracion", "Canal de notificacion creado")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ){
                requestNotificationPermission()
            } else{
                mostrarNotificacionConRetraso()
            }
        } else {
            mostrarNotificacionConRetraso()
        }
        val btnNotificar = findViewById<Button>(R.id.btnNotificar)
        btnNotificar.setOnClickListener {
            try{
                mostrarNotificacionAvanzada()
                Log.d("Depuracion", "Notificacion enviada correctamente")
            }catch (e: Exception) {
                Log.e("Error", "Error al mostrar la notificacion: ${e.message}")
            }
        }

        val btnNotificacionLarga = findViewById<Button>(R.id.btnNotificacionLarga)
        btnNotificacionLarga.setOnClickListener {
            mostrarNotificacionConTextoLargo()
        }
    }

    private fun requestNotificationPermission(){
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted: Boolean ->
            if (isGranted){
                Log.d("Permisos", "Permiso concedido")
                mostrarNotificacionConRetraso()
            } else{
                Log.e("Permisos", "Permiso denegado")
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun mostrarNotificacionConRetraso(){
        window.decorView.postDelayed({
            try {
                mostrarNotificacion()
                Log.d("Depuracion", "Notificacn enviada correctamente")
            }catch (e: Exception) {
                Log.e("Error", "Error al mostrar la notificacion: ${e.message}")
            }
        },2000)
    }

    private fun mostrarNotificacion() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Notificación de prueba")
            .setContentText("Esto es una notificación de prueba.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(1001, notification)
        Log.d("Depuración", "Notificación construida y enviada")
    }

    private fun mostrarNotificacionAvanzada(){
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_notification)
        if(bitmap == null){
            Log.e("Depuracion", "El recurso ic_notification no pudo ser encontrado")
            return
        }
        val intent = Intent(this, P13NotificacionesActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Construir la notificación avanzada
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_accept) // Ícono de la notificación
            .setContentTitle("Notificación Avanzada") // Título
            .setContentText("Texto inicial de la notificación.") // Texto principal
            .setStyle(
                NotificationCompat.BigPictureStyle() // Estilo con imagen grande
                    .bigPicture(bitmap) // Imagen grande
                    .bigLargeIcon(null as Bitmap?) // Sin ícono adicional
            )
            .setContentIntent(pendingIntent) // Acción al tocar
            .setAutoCancel(true) // La notificación desaparece al tocarla
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta
            .build()


        // Enviar la notificación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(1003, notification)
        Log.d("Depuración", "Notificación avanzada enviada")

    }

    private fun mostrarNotificacionConTextoLargo() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_accept) // Ícono de la notificación
            .setContentTitle("Notificación con Texto Largo") // Título
            .setContentText("Texto inicial de la notificación.") // Texto corto visible sin expandir
            .setStyle(
                NotificationCompat.BigTextStyle() // Estilo con texto largo
                    .bigText(
                        "Este es un ejemplo de notificación con texto largo. " +
                                "Puedes añadir información adicional para el usuario en este espacio."
                    )
            )
            .setAutoCancel(true) // Desaparece al tocarla
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta
            .build()


        // Enviar la notificación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(1004, notification)
        Log.d("Depuración", "Notificación con texto largo enviada")
    }

}