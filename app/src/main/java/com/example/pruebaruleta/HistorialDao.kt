package com.example.pruebaruleta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistorialDao {

    // Inserta un nuevo registro en la tabla `historial`
    @Insert
    suspend fun insertarHistorial(historial: Historial)

    // Obtiene el historial de partidas ordenado por el ID de partida en orden descendente
    @Query("SELECT * FROM historial ORDER BY id DESC")
    suspend fun obtenerHistorialCompleto(): List<Historial>
}
