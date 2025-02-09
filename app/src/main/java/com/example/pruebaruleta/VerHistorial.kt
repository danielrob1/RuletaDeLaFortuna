package com.example.pruebaruleta

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class VerHistorial : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val historial = mutableListOf<String>()
    private lateinit var db: FraseDatabase
    private lateinit var historialDao: HistorialDao
    private lateinit var logo: ImageView
    val idioma = Locale.getDefault().language
    var jugador=""
    var ganador=""

    override fun onCreate(savedInstanceState: Bundle?) {
        db = FraseDatabase.getDatabase(this)
        historialDao = db.historialDao()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_historial)
        listView = findViewById(R.id.listView)
        logo = findViewById(R.id.imageView6)
        adapter = ArrayAdapter(this, R.layout.item_historial, R.id.tvHistorial, historial)
        listView.adapter = adapter
        if (idioma == "es") {
            jugador="Jugador "
            ganador="Ganador "
            logo.setImageResource(R.drawable.logo)
        } else if (idioma == "en") {
            jugador="Player "
            ganador="Winner "
            logo.setImageResource(R.drawable.logoen)
        } else{
            jugador="Jugador "
            ganador="Ganador "
            logo.setImageResource(R.drawable.logo)
        }
        // Recuperar los datos de la base de datos
        CoroutineScope(Dispatchers.IO).launch {
            val historialDb = db.historialDao().obtenerHistorialCompleto()
            withContext(Dispatchers.Main) {
                historial.clear()
                historialDb.forEach { historialItem ->
                    val partida = "$jugador 1: ${historialItem.jugador1} - ${historialItem.puntos1} €\n" +
                            "$jugador 2: ${historialItem.jugador2} - ${historialItem.puntos2} €\n" +
                            "$jugador 3: ${historialItem.jugador3} - ${historialItem.puntos3} €\n" +
                            "$ganador : ${historialItem.ganador}"
                    historial.add(partida)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
}
