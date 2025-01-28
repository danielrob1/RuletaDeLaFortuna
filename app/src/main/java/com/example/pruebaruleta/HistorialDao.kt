package com.example.pruebaruleta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistorialDao {

    // Inserta un nuevo registro en la tabla `historial`
    @Insert
    suspend fun insertarHistorial(historial: Historial)

    // Obtiene todo el historial de partidas ordenado por el ID de partida en orden descendente
    @Query("SELECT * FROM historial ORDER BY id DESC")
    suspend fun obtenerHistorialCompleto(): List<Historial>

    // Obtiene el historial filtrado por un jugador específico
    @Query("SELECT * FROM historial WHERE jugador1 = :jugador OR jugador2 = :jugador OR jugador3 = :jugador")
    suspend fun obtenerHistorialPorJugador(jugador: String): List<Historial>

    // Borra todo el historial de partidas
    @Query("DELETE FROM historial")
    suspend fun borrarTodoElHistorial()

    // Obtiene las partidas donde el jugador específico fue el ganador
    @Query("SELECT * FROM historial WHERE ganador = :ganador")
    suspend fun obtenerPartidasPorGanador(ganador: String): List<Historial>
}
