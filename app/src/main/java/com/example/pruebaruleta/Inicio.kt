package com.example.pruebaruleta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
class Inicio: AppCompatActivity() {
    private lateinit var botonEntrar: Button
    private lateinit var editTextJ1: EditText
    private lateinit var editTextJ2: EditText
    private lateinit var editTextJ3: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        botonEntrar = findViewById(R.id.buttonEntrar)
        editTextJ1 = findViewById(R.id.editTextJ1)
        editTextJ2 = findViewById(R.id.editTextJ2)
        editTextJ3 = findViewById(R.id.editTextJ3)
        botonEntrar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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
                startActivity(intent)
            } else{
                intent.putExtra("jugador3", editTextJ3.text.toString())
                startActivity(intent)
            }
        }
    }
}