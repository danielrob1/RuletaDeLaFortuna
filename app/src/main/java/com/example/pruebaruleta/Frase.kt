package com.example.pruebaruleta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frases")
data class Frase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoria: String,
    val frase: String
)
