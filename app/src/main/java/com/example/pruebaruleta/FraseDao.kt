package com.example.pruebaruleta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FraseDao {
    @Insert
    suspend fun insertarFrases(frases: List<Frase>)

    @Query("SELECT * FROM frases WHERE categoria != 'PANEL FINAL' AND idioma='esp'")
    suspend fun obtenerFrasesSinPanelFinalEsp(): List<Frase>

    @Query("SELECT * FROM frases WHERE categoria = 'PANEL FINAL' and idioma='esp'")
    suspend fun obtenerFrasesPanelFinalEsp(): List<Frase>

    @Query("SELECT * FROM frases WHERE categoria != 'FINAL PANEL' AND idioma='eng'")
    suspend fun obtenerFrasesSinPanelFinalEng(): List<Frase>

    @Query("SELECT * FROM frases WHERE categoria = 'FINAL PANEL' and idioma='eng'")
    suspend fun obtenerFrasesPanelFinalEng(): List<Frase>

    @Query("SELECT * FROM frases")
    suspend fun obtenerTodasLasFrases(): List<Frase>


    @Query("DELETE FROM frases")
    suspend fun eliminarTodasLasFrases()
}
