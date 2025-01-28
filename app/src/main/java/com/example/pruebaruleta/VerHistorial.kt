package com.example.pruebaruleta

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerHistorial : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val historial = mutableListOf<String>()
    private lateinit var db: FraseDatabase
    private lateinit var historialDao: HistorialDao

    override fun onCreate(savedInstanceState: Bundle?) {
        db = FraseDatabase.getDatabase(this)
        historialDao = db.historialDao()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_historial)
        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historial)
        listView.adapter = adapter

        // Recuperar los datos de la base de datos
        CoroutineScope(Dispatchers.IO).launch {
            val historialDb = db.historialDao().obtenerHistorialCompleto()
            withContext(Dispatchers.Main) {
                historial.clear()
                historialDb.forEach { historialItem ->
                    val partida = "Jugador 1: ${historialItem.jugador1} - ${historialItem.puntos1} puntos\n" +
                            "Jugador 2: ${historialItem.jugador2} - ${historialItem.puntos2} puntos\n" +
                            "Jugador 3: ${historialItem.jugador3} - ${historialItem.puntos3} puntos\n" +
                            "Ganador: ${historialItem.ganador}"
                    historial.add(partida)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
}
