package com.example.pruebaruleta

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import kotlin.random.Random

class RuletaFinal: AppCompatActivity() {

    private lateinit var ruleta: ImageView
    private lateinit var btnGirar: Button
    private lateinit var gridLayout: GridLayout
    private lateinit var editTextText: EditText
    private lateinit var button: Button
    private lateinit var textViewJ1: TextView
    private lateinit var textViewJ2: TextView
    private lateinit var textViewJ3: TextView
    private lateinit var textViewTurno: TextView
    private  var jugador1 = "Juan"
    private  var jugador2 = "Pedro"
    private  var jugador3 = "Maria"
    private  var turnoDeJugador=1
    private var anguloResultado=0
    private var resultado=""
    private var ruletaGirada=false
    // Valores posibles de la ruleta (números y "Jackpot")
    //private val valoresRuleta = listOf("Jackpot", "1", "2", "3", "4", "5", "6", "7", "8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ruleta = findViewById(R.id.ruleta)
        btnGirar = findViewById(R.id.btnGirar)
        gridLayout = findViewById(R.id.gridLayout)
        editTextText = findViewById(R.id.editTextText)
        button = findViewById(R.id.button)
        textViewJ1 = findViewById(R.id.textViewJ1)
        textViewJ2 = findViewById(R.id.textViewJ2)
        textViewJ3 = findViewById(R.id.textViewJ3)
        textViewTurno = findViewById(R.id.textViewTurno)
        verificarEspacios()
        button.setOnClickListener {
            if(ruletaGirada){
                val letra = editTextText.text.toString().uppercase().firstOrNull()
                if (letra != null) {
                    verificarLetra(letra)
                }
            } else{
                Toast.makeText(this, "La ruleta no se ha girado", Toast.LENGTH_SHORT).show()
            }
            ruletaGirada=false
            editTextText.text.clear()

        }
        btnGirar.setOnClickListener {
            girarRuleta()
        }
        inicializarJugadores(jugador1, jugador2, jugador3)
        seleccionarJugador(turnoDeJugador)
    }
    private fun girarRuleta() {
        anguloResultado=0
        resultado=""
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
            ruletaGirada=true
            anguloResultado = anguloAleatorio % 360
            resultado = obtenerResultado(anguloResultado)
            mostrarResultado(resultado)

        }
        //cargarLetras(frase)
    }
    var jugadores = HashMap<String, Int>()
    private fun inicializarJugadores(jugador1: String, jugador2: String, jugador3: String) {
        jugadores[jugador1] = 0
        jugadores[jugador2] = 0
        jugadores[jugador3] = 0
        textViewJ1.text = jugador1 + ":  " + jugadores[jugador1]
        textViewJ2.text = jugador2 + ":  " + jugadores[jugador2]
        textViewJ3.text = jugador3 + ":  " + jugadores[jugador3]
    }
    // Función que devuelve el resultado según el ángulo final
    private fun obtenerResultado(angulo: Int): String {
        return when (angulo) {
            in 20..59 -> "Jackpot"
            in 60..99 -> "80"
            in 100..139 -> "70"
            in 140..179 -> "60"
            in 180..219 -> "50"
            in 220..259 -> "40"
            in 260..299 -> "30"
            in 300..339 -> "20"
            else -> "10"
        }
    }

    private fun seleccionarJugador(numero:Int):String{
        var jugador = ""
        when(numero){
            1->{
                textViewTurno.text="Es el turno de " + jugador1
                jugador=jugador1
            }
            2->{
                textViewTurno.text="Es el turno de " + jugador2
                jugador=jugador2
            }
            3->{
                textViewTurno.text="Es el turno de " + jugador3
                jugador=jugador3
            }
        }
        return jugador
    }

    private fun mostrarResultado(resultado: String) {
        Toast.makeText(this, "¡Resultado: $resultado!", Toast.LENGTH_SHORT).show()
    }

    val frase = "UN GATO SE CUELA-EN UNA REUNION"

    private fun verificarLetra(letra: Char) {
        var letraEncontrada = false
        for (i in frase.indices) {
            if (frase[i] == letra) {
                letraEncontrada = true
                val imageView = gridLayout.getChildAt(i) as ImageView
                imageView.setImageResource(asignarImagenLetra(letra))
                val valorActual = jugadores[seleccionarJugador(turnoDeJugador)] ?: 0
                if(resultado!="Jackpot"){
                    var valorInt= resultado.toInt();
                    jugadores[seleccionarJugador(turnoDeJugador)] = valorActual + valorInt
                    textViewJ1.text = jugador1 + ":  " + jugadores[jugador1]
                    textViewJ2.text = jugador2 + ":  " + jugadores[jugador2]
                    textViewJ3.text = jugador3 + ":  " + jugadores[jugador3]
                } else{
                    letraEncontrada=false
                }
            }
        }
        if (!letraEncontrada) {
            Toast.makeText(this, "La letra $letra no está en la palabra.", Toast.LENGTH_SHORT).show()
            turnoDeJugador++
            if(turnoDeJugador>3){
                turnoDeJugador=1
            }
            seleccionarJugador(turnoDeJugador)
        }
    }

    private fun asignarImagenLetra(letra: Char): Int {
        return when (letra) {

            'A' -> R.drawable.letra
            'U'-> R.drawable.u
            //'B' -> R.drawable.letra_b
            //'C' -> R.drawable.letra_c
            //'D' -> R.drawable.letra_d
            //'E' -> R.drawable.letra_e
            //'F' -> R.drawable.letra_f
            else -> R.drawable.letra
        }
    }

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

    private fun verificarEspacios() {
        for (i in frase.indices) {
            if (frase[i] == ' ') {
                val imageView = gridLayout.getChildAt(i) as ImageView
                imageView.setImageResource(R.drawable.cuadroazul)
            }
        }
    }

}
