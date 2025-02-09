package com.example.pruebaruleta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pruebaruleta.FraseDatabase.Companion.getDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class Inicio : AppCompatActivity() {
    private lateinit var botonEntrar: Button
    private lateinit var editTextJ1: EditText
    private lateinit var editTextJ2: EditText
    private lateinit var editTextJ3: EditText
    private lateinit var botonHistorial: Button
    private lateinit var logo: ImageView
    val idioma = Locale.getDefault().language

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        botonEntrar = findViewById(R.id.buttonEntrar)
        editTextJ1 = findViewById(R.id.editTextJ1)
        editTextJ2 = findViewById(R.id.editTextJ2)
        editTextJ3 = findViewById(R.id.editTextJ3)
        botonHistorial = findViewById(R.id.buttonHistorial)
        logo = findViewById(R.id.imageView5)
        if (idioma == "es") {
            logo.setImageResource(R.drawable.logo)
        }else if(idioma == "en"){
            logo.setImageResource(R.drawable.logoen)
        }
        botonHistorial.setOnClickListener {
            val intent = Intent(this, VerHistorial::class.java)
            startActivity(intent)
        }

        botonEntrar.setOnClickListener {
            lifecycleScope.launch {
                insertarFrasesEnBaseDeDatos()
                val intent = Intent(this@Inicio, MainActivity::class.java)
                if (editTextJ1.text.toString().isEmpty()) {
                    intent.putExtra("jugador1", "Jugador 1")
                } else {
                    intent.putExtra("jugador1", editTextJ1.text.toString())
                }
                if (editTextJ2.text.toString().isEmpty()) {
                    intent.putExtra("jugador2", "Jugador 2")
                } else {
                    intent.putExtra("jugador2", editTextJ2.text.toString())
                }
                if (editTextJ3.text.toString().isEmpty()) {
                    intent.putExtra("jugador3", "Jugador 3")
                } else {
                    intent.putExtra("jugador3", editTextJ3.text.toString())
                }
                startActivity(intent)
            }
        }
    }

    // Método para leer las frases desde el archivo JSON
    private fun leerFrasesDesdeJson(nombreArchivo: String): List<Frase> {
        val jsonString = assets.open(nombreArchivo).bufferedReader().use { it.readText() }
        val tipoLista = object : TypeToken<List<Frase>>() {}.type
        return Gson().fromJson(jsonString, tipoLista)
    }

    // Método para insertar las frases en la base de datos de forma asíncrona
    private suspend fun insertarFrasesEnBaseDeDatos() {
        try {
            val database = getDatabase(applicationContext) // Obtén la instancia de la base de datos
            // Comprobamos si ya existen frases en la base de datos
            val frasesExistentes = database.fraseDao().obtenerTodasLasFrases()
            if (frasesExistentes.isEmpty()) {
                // Si no hay frases, procedemos con la inserción
                val frases = leerFrasesDesdeJson("frases.json")
                withContext(Dispatchers.IO) {
                    database.fraseDao().insertarFrases(frases)
                }
                withContext(Dispatchers.Main) {
                }
            } else {
                withContext(Dispatchers.Main) {
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Error al insertar las frases: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}
