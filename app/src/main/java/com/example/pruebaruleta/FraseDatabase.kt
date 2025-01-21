package com.example.pruebaruleta

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Frase::class], version = 1)
abstract class FraseDatabase : RoomDatabase() {

    abstract fun fraseDao(): FraseDao

    companion object {
        @Volatile
        private var INSTANCE: FraseDatabase? = null

        fun getDatabase(context: Context): FraseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FraseDatabase::class.java,
                    "frase_database"
                )
                    .addCallback(PrepopulateCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PrepopulateCallback(private val context: Context) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                prepopulateDatabase(context)
            }
        }

        private fun prepopulateDatabase(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val frases = leerFrasesDesdeJson(context, "frases.json")
                val database = getDatabase(context) // No hay recursión, ya está inicializada.
                database.fraseDao().insertarFrases(frases)
            }
        }

        private fun leerFrasesDesdeJson(context: Context, nombreArchivo: String): List<Frase> {
            val jsonString = context.assets.open(nombreArchivo).bufferedReader().use { it.readText() }
            val tipoLista = object : TypeToken<List<Frase>>() {}.type
            return Gson().fromJson(jsonString, tipoLista)
        }
    }
}
