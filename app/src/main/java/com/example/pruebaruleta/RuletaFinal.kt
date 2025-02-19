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
import java.util.Locale
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
    private var idioma = Locale.getDefault().language

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
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Final")
            .setMessage(getString(R.string.mensajeFinal))
            .setPositiveButton("Ok") { _, _ ->
            }
            .create()
        dialog.show()
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
            for (vocal in letras) {
                if (vocal == "A" || vocal == "E" || vocal == "I" || vocal == "O" || vocal == "U") {
                    contadorVocales++
                }
            }

            if (letra.isNotEmpty() && letras.size == 4 && contadorVocales == 1) {
                for (letra in letras) {
                    verificarLetra(letra.first())
                }
            } else {
                button.isEnabled = true
                Toast.makeText(this, getString(R.string.letraValida), Toast.LENGTH_SHORT).show()
            }
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
            if (idioma == "es") {
                frases = fraseDao.obtenerFrasesPanelFinalEsp()
            } else if (idioma == "en") {
                frases = fraseDao.obtenerFrasesPanelFinalEng()
            } else{
                frases = fraseDao.obtenerFrasesPanelFinalEsp()
            }
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

        val animador = ObjectAnimator.ofFloat(ruleta, "rotation", 0f, anguloFinal.toFloat())
        animador.duration = 5000
        animador.interpolator = DecelerateInterpolator()
        animador.start()
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
        btnResolver.setOnClickListener {
            resolverFrase()
        }


    }
    private fun resolverFrase() {
        val editTextFrase = EditText(this)
        editTextFrase.gravity = android.view.Gravity.CENTER
        editTextFrase.setTextColor(Color.BLACK)
        editTextFrase.hint = getString(R.string.resolverHint)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Comprobar frase")
            .setMessage(getString(R.string.resolverMensaje))
            .setView(editTextFrase)
            .setPositiveButton(getString(R.string.resolverComprobar)) { _, _ ->
                val fraseIntroducida = editTextFrase.text.toString().uppercase()

                if (fraseIntroducida == frase) {
                    jugadores[jugadorFinal] = jugadores[jugadorFinal]!! + resultado.toInt()
                    Toast.makeText(this, getString(R.string.resolverCorrecto), Toast.LENGTH_LONG).show()
                    irAPantallaFinal(true)
                } else {
                    Toast.makeText(this, getString(R.string.resolverIncorrecto) +" " + frase, Toast.LENGTH_SHORT).show()
                    irAPantallaFinal(false)
                }
            }
            .setNegativeButton(getString(R.string.resolverCancelar)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
    private fun inicializarJugadores(jugador: String) {
        textViewJ1.text = jugador + ": " + jugadores[jugador]
    }
    private fun obtenerResultado(angulo: Int): String {
        return angulo.toString()
    }
    private fun irAPantallaFinal(haGanado: Boolean) {
        mediaPlayer?.release()
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
        finish()
    }
    private fun mostrarResultado(resultado: String) {
        Toast.makeText(this, getString(R.string.toastResultaado) +  resultado + "!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, getString(R.string.toastNoEsta) + letra, Toast.LENGTH_SHORT).show()
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
            else -> R.drawable.cuadroblanco
        }
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
        mediaPlayer?.release()
        mediaPlayer = null
    }


}
