package com.example.pruebaruleta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FraseDao {
    @Insert
    suspend fun insertarFrases(frases: List<Frase>)

    @Query("SELECT * FROM frases WHERE categoria != 'PANEL FINAL'")
    suspend fun obtenerFrasesSinPanelFinal(): List<Frase>

    @Query("SELECT * FROM frases WHERE categoria = 'PANEL FINAL'")
    suspend fun obtenerFrasesPanelFinal(): List<Frase>

    @Query("SELECT * FROM frases")
    suspend fun obtenerTodasLasFrases(): List<Frase>


    @Query("DELETE FROM frases")
    suspend fun eliminarTodasLasFrases()
}
