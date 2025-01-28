package com.example.pruebaruleta

import android.animation.ObjectAnimator
import android.content.Intent
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

class RuletaFinal : AppCompatActivity() {

    private lateinit var ruleta: ImageView
    private lateinit var btnGirar: Button
    private lateinit var gridLayout: GridLayout
    private lateinit var editTextText: EditText
    private lateinit var button: Button
    private lateinit var textViewJ1: TextView
    private lateinit var textViewTurno: TextView
    private lateinit var btnResolver: Button
    private var jugadorFinal = ""
    private var puntosJugadorFinal = 0
    private lateinit var jugadoresRestantes: List<String>
    private lateinit var puntosRestantes: List<Int>
    private lateinit var jugadores: HashMap<String, Int>
    private var anguloResultado = 0
    private var resultado = ""
    private var ruletaGirada = false


    // Valores posibles de la ruleta (números y "Jackpot")
    //private val valoresRuleta = listOf("Jackpot", "1", "2", "3", "4", "5", "6", "7", "8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panelfinal)
        ruleta = findViewById(R.id.ruleta)
        btnGirar = findViewById(R.id.btnGirar)
        gridLayout = findViewById(R.id.gridLayout)
        editTextText = findViewById(R.id.editTextText)
        button = findViewById(R.id.button)
        textViewJ1 = findViewById(R.id.textViewJ1)
        textViewTurno = findViewById(R.id.textViewTurno)
        btnResolver = findViewById(R.id.btnResolver)
        verificarEspacios()
        button.setOnClickListener {
            var letra = editTextText.text.toString().uppercase()
            var letras = letra.split(" ")
            var contadorVocales = 0

            // Contar las vocales en la entrada
            for (vocal in letras) {
                if (vocal == "A" || vocal == "E" || vocal == "I" || vocal == "O" || vocal == "U") {
                    contadorVocales++
                }
            }

            // Verificar el formato
            if (letra.isNotEmpty() && letras.size == 4 && contadorVocales == 1) {
                for (letra in letras) {
                    verificarLetra(letra.first()) // Procesar las letras si el formato es correcto
                }
            } else {
                // Mostrar mensajes de error dependiendo del problema
                if (letras.size != 4) {
                    Toast.makeText(this, "Introduce 4 palabras", Toast.LENGTH_SHORT).show()
                }
                if (contadorVocales != 1) {
                    Toast.makeText(this, "Introduce solo una vocal", Toast.LENGTH_SHORT).show()
                }
                if (letras.isEmpty()) {
                    Toast.makeText(this, "Introduce una letra", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "Error en el formato", Toast.LENGTH_SHORT).show()
            }

            // Reiniciar variables al final
            ruletaGirada = false
            editTextText.text.clear()
        }

        btnGirar.setOnClickListener {
            girarRuleta()
        }
        jugadorFinal = intent.getStringExtra("jugadorFinal") ?: ""
        puntosJugadorFinal = intent.getIntExtra("puntosJugadorFinal", 0)
        jugadoresRestantes = intent.getStringArrayListExtra("nombresRestantes") ?: emptyList()
        puntosRestantes = intent.getIntegerArrayListExtra("puntosRestantes") ?: emptyList()
        jugadores = intent.getSerializableExtra("jugadores") as HashMap<String, Int>

        if (jugadorFinal != null) {
            inicializarJugadores(jugadorFinal)
        }
    }

    val frase = "UN GATO SE CUELA EN UNA REUNION"
    var letrasIniciales = listOf('R','S','F','O')

    val fraseSinEspacios=frase.replace(" ", "")
    var longitudFrase=fraseSinEspacios.length
    var letrasLevantadas=0;
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
            for (letra in letrasIniciales) {
                verificarLetra(letra)
            }
        }
        //cargarLetras(frase)
        btnResolver.setOnClickListener {
            resolverFrase()
        }


    }
    private fun resolverFrase() {
        // Crear un EditText para que el usuario introduzca la frase
        val editTextFrase = EditText(this)
        editTextFrase.hint = "Introduce la frase completa"
        // Construir el AlertDialog
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Comprobar frase")
            .setMessage("Introduce la frase completa para comprobar si has acertado:")
            .setView(editTextFrase) // Añadir el EditText al diálogo
            .setPositiveButton("Comprobar") { _, _ ->
                val fraseIntroducida = editTextFrase.text.toString().uppercase()
                // Comprobar si la frase introducida coincide con la solución
                if (fraseIntroducida == frase) {
                    Toast.makeText(this, "¡Correcto! Has acertado la frase.", Toast.LENGTH_LONG).show()
                    irAPantallaFinal(true)
                } else {
                    Toast.makeText(this, "Incorrecto, la frase era $frase", Toast.LENGTH_SHORT).show()
                    irAPantallaFinal(false)
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Cerrar el diálogo si el usuario cancela
            }
            .create()

        // Mostrar el diálogo
        dialog.show()
    }
    private fun inicializarJugadores(jugador: String) {
        textViewJ1.text = jugador
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

    /**
     *  private fun seleccionarJugador(numero:Int):String{
     *         var jugador = ""
     *         when(numero){
     *             1->{
     *                 textViewTurno.text="Es el turno de " + jugador
     *                 jugador=jugador1
     *             }
     *             2->{
     *                 textViewTurno.text="Es el turno de " + jugador2
     *                 jugador=jugador2
     *             }
     *             3->{
     *                 textViewTurno.text="Es el turno de " + jugador3
     *                 jugador=jugador3
     *             }
     *         }
     *         return jugador
     *     }
     */

    private fun irAPantallaFinal(haGanado: Boolean) {
        val intent = Intent(this, PantallaFinal::class.java).apply {
            putExtra("jugadorFinal", jugadorFinal)
            putExtra("haGanado", haGanado)
            putExtra("puntosJugadorFinal", puntosJugadorFinal)
            putStringArrayListExtra("jugadoresRestantes", ArrayList(jugadoresRestantes))
            putIntegerArrayListExtra("puntosRestantes", ArrayList(puntosRestantes))
            putExtra("jugadores", jugadores)
        }
        startActivity(intent)
        finish() // Cierra la actividad actual
    }
    private fun mostrarResultado(resultado: String) {
        if(resultado=="Jackpot"){
            verificarLetra(' ')
        }
        Toast.makeText(this, "¡Resultado: $resultado!", Toast.LENGTH_SHORT).show()
    }



    private fun verificarLetra(letra: Char) {
            var letraEncontrada = false
            for (i in frase.indices) {
                if (frase[i] == letra) {
                    letraEncontrada = true
                    letrasLevantadas++
                    val imageView = gridLayout.getChildAt(i) as ImageView
                    imageView.setImageResource(asignarImagenLetra(letra))
                }
            }
            if (!letraEncontrada) {
                Toast.makeText(this, "La letra $letra no está en la palabra.", Toast.LENGTH_SHORT).show()
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
