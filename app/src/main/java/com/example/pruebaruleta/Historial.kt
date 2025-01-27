package com.example.pruebaruleta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial")
data class Historial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jugador1: String,
    val jugador2: String,
    val jugador3: String,
    val puntos1: Int,
    val puntos2: Int,
    val puntos3: Int,
    val ganador: String
)
