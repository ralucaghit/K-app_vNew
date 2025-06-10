package com.example.k_app_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Nivel23ComparareActivity : AppCompatActivity() {
    private lateinit var numarSt: TextView
    private lateinit var numarDr: TextView
    private lateinit var semnText: TextView
    private lateinit var feedbackText: TextView
    private lateinit var buttonMaiMic: Button
    private lateinit var buttonEgal: Button
    private lateinit var buttonMaiMare: Button
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var exercitiuCurent = 0
    private val totalExercitii = 15
    private var st: Int = 0
    private var dr: Int = 0
    private var corect: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nivel23_comparare)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        numarSt = findViewById(R.id.numarStanga)
        numarDr = findViewById(R.id.numarDreapta)
        semnText = findViewById(R.id.semnText)
        buttonMaiMic = findViewById(R.id.btnMaiMic)
        buttonEgal = findViewById(R.id.btnEgal)
        buttonMaiMare = findViewById(R.id.btnMaiMare)
        feedbackText = findViewById(R.id.feedbackText)
        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        buttonMaiMic.text = "<"
        buttonEgal.text = "="
        buttonMaiMare.text = ">"

        findViewById<Button>(R.id.btnMaiMic).setOnClickListener { verifica("<") }
        findViewById<Button>(R.id.btnEgal).setOnClickListener { verifica("=") }
        findViewById<Button>(R.id.btnMaiMare).setOnClickListener { verifica(">") }

        genereazaExercitiu()
    }

    private fun genereazaExercitiu() {

        val nivel = intent.getStringExtra("nivel") ?: "mediu"
        val limitaMax = if (nivel == "expert") 100 else 30

        st = (1..limitaMax).random()
        dr = (1..limitaMax).random()

        numarSt.text = st.toString()
        numarDr.text = dr.toString()
        semnText.text = "?"

        corect = when {
            st > dr -> ">"
            st < dr -> "<"
            else -> "="
        }

        feedbackText.text = ""
    }

    private fun verifica(alegere: String) {
        if (alegere == corect) {
            corectSound.start()
            feedbackText.text = getString(R.string.mesaj_bravo)
            semnText.text = alegere

            // Animatie pe text
            val scaleX = ObjectAnimator.ofFloat(semnText, "scaleX", 1.2f, 2f, 1.2f)
            val scaleY = ObjectAnimator.ofFloat(semnText, "scaleY", 1.2f, 2f, 1.2f)

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 600
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()

            exercitiuCurent++

            if (exercitiuCurent >= totalExercitii) {
                arataRezultatulFinal()
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    genereazaExercitiu()
                }, 1500)
            }


        } else {
            gresitSound.start()
            feedbackText.text = getString(R.string.mesaj_mai_incearca)
        }
    }

    private fun arataRezultatulFinal() {
        feedbackText.text = getString(R.string.mesaj_felicitari)

        salveazaProgres()

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 3000)
    }

    private fun salveazaProgres() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val nivel = intent.getStringExtra("nivel") ?: "mediu"

        val document: String = if(nivel == "expert"){
            "nivel3"
        } else {
            "nivel2"
        }

        if (userId != null) {
            val progres = hashMapOf(
                "terminat" to true,
                "data" to FieldValue.serverTimestamp()
            )

            FirebaseFirestore.getInstance()
                .collection("copii")
                .document(userId)
                .collection("progresComparare")
                .document(document)
                .set(progres)
                .addOnSuccessListener {
                    Log.d("FIREBASE_SAVE", "Progres salvat!")
                }

                .addOnFailureListener { e ->
                    Log.e("FIREBASE_SAVE", "Eroare la salvarea progresului: ${e.message}", e)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        corectSound.release()
        gresitSound.release()
    }
}