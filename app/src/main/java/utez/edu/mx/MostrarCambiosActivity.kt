package utez.edu.mx

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MostrarCambiosActivity : AppCompatActivity() {

    private lateinit var tvSavedData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_cambios)

        // Inicializar el TextView para mostrar los datos guardados
        tvSavedData = findViewById(R.id.tv_saved_data)

        // Leer el archivo y mostrar su contenido
        displaySavedData()
    }

    // Función para leer el archivo y mostrar los datos
    private fun displaySavedData() {
        try {
            // Obtén el archivo de datos guardados
            val file = File(filesDir, "datos.txt")

            // Verifica si el archivo existe
            if (file.exists()) {
                // Lee el contenido del archivo
                val data = file.readText()
                // Muestra los datos en el TextView
                tvSavedData.text = data
            } else {
                // Si el archivo no existe, muestra un mensaje informando que no se encontraron datos
                tvSavedData.text = "No se encontraron datos guardados."
            }
        } catch (e: Exception) {
            // Si ocurre algún error, muestra un mensaje de error
            tvSavedData.text = "Error al cargar los datos."
        }
    }
}
