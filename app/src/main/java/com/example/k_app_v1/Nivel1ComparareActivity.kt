package com.example.k_app_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
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
        feedbackText = findViewById(R.id.feedbackText)
        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        imagineStanga.setOnClickListener{
            verificaRaspuns(">")
        }

        imagineDreapta.setOnClickListener{
            verificaRaspuns("<")
        }

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("Matematica").document("matematica")
            .collection("comparare")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    exercitii.add(document.data)
                }
                if (exercitii.isNotEmpty()) {
                    afiseazaExercitiu()
                } else {
                    Toast.makeText(this, "Nu s-au găsit exerciții.", Toast.LENGTH_SHORT).show()
                }
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

            // Animatie pe text
            val scaleX = ObjectAnimator.ofFloat(textSemn, "scaleX", 1.2f, 2f, 1.2f)
            val scaleY = ObjectAnimator.ofFloat(textSemn, "scaleY", 1.2f, 2f, 1.2f)

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 600
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()

            Handler(Looper.getMainLooper()).postDelayed({
                indexCurent++
                afiseazaExercitiu()
            }, 2000)
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
                .collection("progresComparare")
                .document("nivel1")
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