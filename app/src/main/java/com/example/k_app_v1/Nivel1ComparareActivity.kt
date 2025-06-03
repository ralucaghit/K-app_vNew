package com.example.k_app_v1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Nivel1ComparareActivity : AppCompatActivity() {

    private lateinit var imagineStanga: ImageView
    private lateinit var imagineDreapta: ImageView
    private lateinit var textSemn: TextView
    private lateinit var textMaiMic: TextView
    private lateinit var textEgal: TextView
    private lateinit var textMaiMare: TextView
    private lateinit var buttonMaiMic: Button
    private lateinit var buttonEgal: Button
    private lateinit var buttonMaiMare: Button
    private lateinit var feedbackText: TextView
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var raspunsCorect: String = ""
    private var exercitii = mutableListOf<Map<String, Any>>()
    private var indexCurent = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel1_comparare)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagineStanga = findViewById(R.id.imagineStanga)
        imagineDreapta = findViewById(R.id.imagineDreapta)
        textSemn = findViewById(R.id.textSemn)
        textMaiMic = findViewById(R.id.textMaiMic)
        textEgal = findViewById(R.id.textEgal)
        textMaiMare = findViewById(R.id.textMaiMare)
        buttonMaiMic = findViewById(R.id.buttonMaiMic)
        buttonEgal = findViewById(R.id.buttonEgal)
        buttonMaiMare = findViewById(R.id.buttonMaiMare)
        feedbackText = findViewById(R.id.feedbackText)
        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        buttonMaiMic.text = "<"
        buttonEgal.text = "="
        buttonMaiMare.text = ">"
        textMaiMic.text = "semnul < înseamnă mai mic decât"
        textEgal.text = "semnul = înseamnă egal cu"
        textMaiMare.text = "semnul > înseamnă mai mare decât"

        findViewById<Button>(R.id.buttonMaiMic).setOnClickListener { verificaRaspuns("<") }
        findViewById<Button>(R.id.buttonEgal).setOnClickListener { verificaRaspuns("=") }
        findViewById<Button>(R.id.buttonMaiMare).setOnClickListener { verificaRaspuns(">") }

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("Matematica").document("matematica")
            .collection("comparare").document("incepator")
            .collection("incepator")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    exercitii.add(document.data)
                }
                exercitii.shuffle()
                afiseazaExercitiu()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Eroare la încărcare exerciții", Toast.LENGTH_SHORT).show()
            }
    }

    private fun afiseazaExercitiu() {
        if (indexCurent >= exercitii.size) {
            feedbackText.text = getString(R.string.mesaj_felicitari)

            salveazaProgres()

            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 2500)
            return
        }

        val exercitiu = exercitii[indexCurent]
        val imgSt = exercitiu["imagineSt"] as String
        val imgDr = exercitiu["imagineDr"] as String
        raspunsCorect = exercitiu["corect"] as String

        Glide.with(this).load(imgSt).into(imagineStanga)
        Glide.with(this).load(imgDr).into(imagineDreapta)
        textSemn.text = "?"
        feedbackText.text = ""
    }

    private fun verificaRaspuns(raspuns: String) {
        if (raspuns == raspunsCorect) {
            corectSound.start()
            feedbackText.text = getString(R.string.mesaj_bravo)
            textSemn.text = raspuns
            Handler(Looper.getMainLooper()).postDelayed({
                indexCurent++
                afiseazaExercitiu()
            }, 1500)
        } else {
            gresitSound.start()
            feedbackText.text = getString(R.string.mesaj_mai_incearca)
        }
    }

    private fun salveazaProgres() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val progres = hashMapOf(
                "terminat" to true,
                "data" to FieldValue.serverTimestamp()
            )

            FirebaseFirestore.getInstance()
                .collection("copii")
                .document(userId)
                .collection("progresComparare1")
                .document()
                .set(progres)
                .addOnSuccessListener {
                    Log.d("FIREBASE_SAVE", "Progres salvat!")
                }

                .addOnFailureListener { e ->
                    Log.e("FIREBASE_SAVE", "Eroare la salvarea progresului: ${e.message}", e)
                }
        }
    }
}