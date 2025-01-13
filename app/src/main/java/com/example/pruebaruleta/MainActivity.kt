package com.example.pruebaruleta

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var ruleta: ImageView
    private lateinit var btnGirar: Button

    // Valores posibles de la ruleta (números y "Jackpot")
    //private val valoresRuleta = listOf("Jackpot", "1", "2", "3", "4", "5", "6", "7", "8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ruleta = findViewById(R.id.ruleta)
        btnGirar = findViewById(R.id.btnGirar)

        btnGirar.setOnClickListener {
            girarRuleta()
        }
    }

    private fun girarRuleta() {
        // Seleccionar un valor aleatorio de giro entre 0 y 360 grados
        val anguloAleatorio = Random.nextInt(360)
        val vueltasCompletas = 360 * 5 // 5 vueltas completas antes de detenerse
        val anguloFinal = anguloAleatorio + vueltasCompletas

        // Animar el giro de la ruleta
        val animador = ObjectAnimator.ofFloat(ruleta, "rotation", 0f, anguloFinal.toFloat())
        animador.duration = 5000 // Duración de 3 segundos
        animador.interpolator = DecelerateInterpolator() // Interpolador para desacelerar el giro
        animador.start()

        // Mostrar el resultado cuando termina la animación
        animador.doOnEnd {
            val anguloResultado = anguloAleatorio % 360
            val resultado = obtenerResultado(anguloResultado)
            mostrarResultado(resultado)
        }
        cargarLetras(frase)
    }

    // Función que devuelve el resultado según el ángulo final
    private fun obtenerResultado(angulo: Int): String {
        return when (angulo) {
            in 20..59 -> "Jackpot"
            in 60..99 -> "8"
            in 100..139 -> "7"
            in 140..179 -> "6"
            in 180..219 -> "5"
            in 220..259 -> "4"
            in 260..299 -> "3"
            in 300..339 -> "2"
            else -> "1"
        }
    }

    private fun mostrarResultado(resultado: String) {
        Toast.makeText(this, "¡Resultado: $resultado!", Toast.LENGTH_SHORT).show()
    }

    val frase = "UN GATO SE CUELA-EN UNA REUNION"

    private fun cargarLetras(frase: String) {
        val fraseCompleta = frase.toCharArray()
        val frase1 = ArrayList<Char>()
        val frase2 = ArrayList<Char>()
        var mostrarString1 = ""
        var mostrarString2 = ""

        // Llenar frase1 con los primeros 16 caracteres
        for (i in 0 until 16) {
            frase1.add(fraseCompleta[i])  // Usar 'add' para agregar caracteres
            mostrarString1 += frase1[i]
        }

        // Llenar frase2 con los caracteres restantes
        for (j in 16 until fraseCompleta.size) {
            frase2.add(fraseCompleta[j])  // Usar 'add' para agregar caracteres
            mostrarString2 += frase2[j - 16]  // Ajustar el índice para concatenar correctamente
        }

        // Mostrar los resultados
        Toast.makeText(this, mostrarString1, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, mostrarString2, Toast.LENGTH_SHORT).show()
    }


}
