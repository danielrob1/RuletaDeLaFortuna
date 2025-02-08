package com.example.pruebaruleta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PantallaFinal : AppCompatActivity() {
    private lateinit var db: FraseDatabase
    private lateinit var historialDao: HistorialDao

    override fun onCreate(savedInstanceState: Bundle?) {
        db = FraseDatabase.getDatabase(this)
        historialDao = db.historialDao()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_final)
        val btnInicio: Button = findViewById(R.id.btnInicio)
        val btnHistorial: Button = findViewById(R.id.btnHistorial)
        val textViewResultado: TextView = findViewById(R.id.textViewResultado)
        val textViewJugadorFinal: TextView = findViewById(R.id.textViewJugadorFinal)
        val textViewJugadoresRestantes: TextView = findViewById(R.id.textViewJugadoresRestantes)

        // Recuperar los datos del Intent
        val jugadorFinal = intent.getStringExtra("jugadorFinal") ?: "Desconocido"
        val haGanado = intent.getBooleanExtra("haGanado", false)
        val puntosJugadorFinal = intent.getIntExtra("puntosJugadorFinal", 0)
        val jugadoresRestantes = intent.getStringArrayListExtra("jugadoresRestantes") ?: listOf()
        val puntosRestantes = intent.getIntegerArrayListExtra("puntosRestantes") ?: listOf()

        //Datos del intent para el  historial
        val jugadores =
            intent.getSerializableExtra("jugadores") as? HashMap<String, Int> ?: HashMap()
        val jugador1 = jugadores.keys.elementAtOrNull(0) ?: "Jugador1"
        val puntos1 = jugadores[jugador1] ?: 0
        val jugador2 = jugadores.keys.elementAtOrNull(1) ?: "Jugador2"
        val puntos2 = jugadores[jugador2] ?: 0
        val jugador3 = jugadores.keys.elementAtOrNull(2) ?: "Jugador3"
        val puntos3 = jugadores[jugador3] ?: 0

        // Mostrar el resultado del jugador final
        textViewResultado.text = if (haGanado) {
            "¡$jugadorFinal ha ganado con $puntosJugadorFinal puntos!"
        } else {
            "¡$jugadorFinal ha perdido con $puntosJugadorFinal puntos!"
        }
        guardarEnHistorial(
            jugador1,
            jugador2,
            jugador3,
            puntos1,
            puntos2,
            puntos3,
            jugadorFinal,
            haGanado
        )
        // Mostrar los jugadores restantes y sus puntos
        val textoRestantes = StringBuilder("Jugadores restantes:\n")
        for (i in jugadoresRestantes.indices) {
            textoRestantes.append("${jugadoresRestantes[i]}: ${puntosRestantes[i]} puntos\n")
        }
        textViewJugadoresRestantes.text = textoRestantes.toString()
        btnHistorial.setOnClickListener {
            val intent = Intent(this, VerHistorial::class.java)
            startActivity(intent)
        }
        btnInicio.setOnClickListener {
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }
    }
    private fun guardarEnHistorial(jugador1: String, jugador2: String, jugador3: String, puntos1: Int, puntos2: Int, puntos3: Int, ganador: String, haGanado: Boolean) {
        lateinit var historial: Historial
        if(haGanado){
             historial = Historial(
                jugador1 = jugador1,
                jugador2 = jugador2,
                jugador3 = jugador3,
                puntos1 = puntos1,
                puntos2 = puntos2,
                puntos3 = puntos3,
                ganador = ganador
            )
        }else{
             historial = Historial(
                jugador1 = jugador1,
                jugador2 = jugador2,
                jugador3 = jugador3,
                puntos1 = puntos1,
                puntos2= puntos2,
                puntos3 = puntos3,
                ganador = "Sin ganador"
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            db.historialDao().insertarHistorial(historial)
        }
    }
}
