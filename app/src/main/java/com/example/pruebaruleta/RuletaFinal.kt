package com.example.pruebaruleta

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RuletaFinal : AppCompatActivity() {
    private lateinit var  db: FraseDatabase
    private lateinit var fraseDao: FraseDao
    private lateinit var ruleta: ImageView
    private lateinit var btnGirar: Button
    private  var panel:MutableList<ImageView> = mutableListOf()
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
    private var sectores = listOf(8000,1000,2000,3000,4000,5000,6000,7000,8000,1000,2000,3000,4000,5000,6000,7000)
    private var sectoresAngulos= IntArray(sectores.size)
    private var grados = 0
    private lateinit var frases: List<Frase>
    private lateinit var frase: String
    private lateinit var fraseSinEspacios: String
    private  var longitudFrase=0
    private  var letrasLevantadas=0
    private var mediaPlayer: MediaPlayer? = null



    // Valores posibles de la ruleta (números y "Jackpot")
    //private val valoresRuleta = listOf("Jackpot", "1", "2", "3", "4", "5", "6", "7", "8")

    override fun onCreate(savedInstanceState: Bundle?) {
        db = FraseDatabase.getDatabase(this)
        fraseDao= db.fraseDao()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panelfinal)
        ruleta = findViewById(R.id.ruleta)
        btnGirar = findViewById(R.id.btnGirar)
        editTextText = findViewById(R.id.editTextText)
        button = findViewById(R.id.button)
        textViewJ1 = findViewById(R.id.textViewJ1)
        btnResolver = findViewById(R.id.btnResolver)
        button.isEnabled = false
        frase=""
        mediaPlayer = MediaPlayer.create(this, R.raw.musicafondo)
        mediaPlayer?.start()
        obtenerGradosPorSectores()
        for (i in 0..31) {
            val id = resources.getIdentifier("hueco$i", "id", packageName)
            val imageView = findViewById<ImageView>(id)
            panel.add(imageView)
        }
        button.setOnClickListener {
            button.isEnabled = false
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
                button.isEnabled = true
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
        CoroutineScope(Dispatchers.IO).launch {
            frases = fraseDao.obtenerFrasesPanelFinal()
            withContext(Dispatchers.Main) {
                if (frases.isNotEmpty()) {
                    val aleatorio = Random.nextInt(0, frases.size)
                    //frase="UN GATO SE CUELA EN UNA REUNION"
                    frase = frases[aleatorio].frase
                    fraseSinEspacios = frase.replace(" ", "")
                    longitudFrase = fraseSinEspacios.length
                    letrasLevantadas = 0
                    verificarEspacios()
                } else {
                    Toast.makeText(this@RuletaFinal, "No se encontraron frases en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    var letrasIniciales = listOf('R','S','F','O')
    private fun girarRuleta() {
        btnGirar.isEnabled = false
        val random = Random.Default
        grados = random.nextInt(sectores.size-1)
        resultado=""
        val anguloFinal=(360* sectores.size) + sectoresAngulos[grados]


        // Animar el giro de la ruleta
        val animador = ObjectAnimator.ofFloat(ruleta, "rotation", 0f, anguloFinal.toFloat())
        animador.duration = 5000 // Duración de 3 segundos
        animador.interpolator = DecelerateInterpolator() // Interpolador para desacelerar el giro
        animador.start()

        // Mostrar el resultado cuando termina la animación
        animador.doOnEnd {
            ruletaGirada=true
            button.isEnabled = true
            anguloResultado = sectores[sectores.size-(grados +1)]
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
        editTextFrase.gravity = android.view.Gravity.CENTER
        editTextFrase.setTextColor(Color.BLACK)
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
                    jugadores[jugadorFinal] = jugadores[jugadorFinal]!! + resultado.toInt()
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
        textViewJ1.text = jugador + ": " + jugadores[jugador]
    }
    // Función que devuelve el resultado según el ángulo final
    private fun obtenerResultado(angulo: Int): String {
        return angulo.toString()
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
        mediaPlayer?.release() // Liberar recursos cuando la app se cierra
        mediaPlayer = null
        val intent = Intent(this, PantallaFinal::class.java).apply {
            putExtra("jugadorFinal", jugadorFinal)
            putExtra("haGanado", haGanado)
            if(haGanado){
                puntosJugadorFinal += resultado.toInt()

            }
            putExtra("puntosJugadorFinal", puntosJugadorFinal)
            putStringArrayListExtra("jugadoresRestantes", ArrayList(jugadoresRestantes))
            putIntegerArrayListExtra("puntosRestantes", ArrayList(puntosRestantes))
            putExtra("jugadores", jugadores)
        }
        startActivity(intent)
        finish() // Cierra la actividad actual
    }
    private fun mostrarResultado(resultado: String) {
        Toast.makeText(this, "¡Resultado: $resultado!", Toast.LENGTH_SHORT).show()
    }



    private fun verificarLetra(letra: Char) {
            var letraEncontrada = false
            for (i in frase.indices) {
                if (frase[i] == letra) {
                    letraEncontrada = true
                    letrasLevantadas++
                    val imageView = panel[i]
                    imageView.setImageResource(asignarImagenLetra(letra))
                }
            }
            if (!letraEncontrada) {
                Toast.makeText(this, "La letra $letra no está en la palabra.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun asignarImagenLetra(letra: Char): Int {
        return when (letra) {
            'A' -> R.drawable.a
            'B' -> R.drawable.b
            'C' -> R.drawable.c
            'D' -> R.drawable.d
            'E' -> R.drawable.e
            'F' -> R.drawable.f
            'G' -> R.drawable.g
            'H' -> R.drawable.h
            'I' -> R.drawable.i
            'J' -> R.drawable.j
            'K' -> R.drawable.k
            'L' -> R.drawable.l
            'M' -> R.drawable.m
            'N' -> R.drawable.n
            'Ñ' -> R.drawable.nn
            'O' -> R.drawable.o
            'P' -> R.drawable.p
            'Q' -> R.drawable.q
            'R' -> R.drawable.r
            'S' -> R.drawable.s
            'T' -> R.drawable.t
            'U' -> R.drawable.u
            'V' -> R.drawable.v
            'W' -> R.drawable.w
            'X' -> R.drawable.x
            'Y' -> R.drawable.y
            'Z' -> R.drawable.z
            else -> R.drawable.cuadroblanco // Default image for unknown characters
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
                val imageView =panel[i]
                imageView.setImageResource(R.drawable.cuadroazul)
            }
        }
        if (frase.length < 32) {
            for (i in frase.length until 32) {
                val imageView = panel[i]
                imageView.setImageResource(R.drawable.cuadroazul)
            }
        }
    }

    private fun obtenerGradosPorSectores(){
        val gradoSector = 360/sectores.size
        for(i in sectores.indices){
            sectoresAngulos[i]=(i+1) * gradoSector
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Liberar recursos cuando la app se cierra
        mediaPlayer = null
    }


}
