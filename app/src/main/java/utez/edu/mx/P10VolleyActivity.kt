package utez.edu.mx


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import utez.edu.mx.adapters.UserAdapter
import utez.edu.mx.models.User

class P10VolleyActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_p10_volley)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsers()
    }

    private fun fetchUsers() {
        val url =  "https://jsonplaceholder.typicode.com/users"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null ,
            { response ->
                val users = ArrayList<User>()
                for (i in 0 until response.length()){
                    val userJson = response.getJSONObject(i)
                    val name = userJson.getString("name")
                    val email = userJson.getString("email")
                    users.add(User(name, email))
                }
                userAdapter = UserAdapter(users)
                recyclerView.adapter = userAdapter
            },
            { error ->
                Log.e("Volley", "Error: ${error.message}")
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }
}