package com.example.k_app_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ScadereNivel1Activity : AppCompatActivity() {
    private lateinit var imagine: ImageView
    private lateinit var textSemn: TextView
    private lateinit var textNumar: TextView
    private lateinit var egal: TextView
    private lateinit var rezultat: TextView
    private lateinit var butoaneGrid: GridLayout
    private lateinit var feedbackText: TextView
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var raspunsCorect: Int = 0
    private var exercitii = mutableListOf<Map<String, Any>>()
    private var indexCurent = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scadere_nivel1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagine = findViewById(R.id.imagine)
        textSemn = findViewById(R.id.textSemn)
        textNumar = findViewById(R.id.textNumar)
        egal = findViewById(R.id.egal)
        rezultat = findViewById(R.id.rezultat)
        butoaneGrid = findViewById(R.id.gridNumere)
        feedbackText = findViewById(R.id.feedbackText)
        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        textSemn.text = "-"
        egal.text = "="


        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("Matematica").document("matematica")
            .collection("scadere")
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
        val imgSt = exercitiu["imagine"] as String
        textNumar.text = exercitiu["numar"] as String
        raspunsCorect = (exercitiu["corect"] as Long).toInt()

        Glide.with(this).load(imgSt).into(imagine)

        feedbackText.text = ""
        rezultat.text = "?"

        val variante = mutableSetOf<Int>()
        variante.add(raspunsCorect)
        while (variante.size < 4) {
            variante.add((0..10).random())
        }

        val colors = listOf("#FFF9C4", "#F8BBD0", "#F8DAC5", "#B2EBF2")
        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

        val varianteShuffled = variante.shuffled()
        butoaneGrid.removeAllViews()

        for ((index, varianta) in varianteShuffled.withIndex()) {
            val btn = Button(this).apply {
                text = varianta.toString()
                isAllCaps = false
                gravity = Gravity.CENTER
                includeFontPadding = false
                textSize = 28f

                if (index < colors.size) {
                    background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
                    background?.setTint(colors[index].toColorInt())
                }

                setTextColor(Color.DKGRAY)
                typeface?.let { this.typeface = it }

                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 180
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(20, 24, 20, 24)
                }

                setOnClickListener {
                    verificaRaspuns(varianta, this)
                }
            }
            butoaneGrid.addView(btn)
        }
    }

    private fun verificaRaspuns(selectat: Int, btn: Button) {
        if (selectat == raspunsCorect) {
            corectSound.start()
            feedbackText.text = getString(R.string.mesaj_bravo)
            rezultat.text = raspunsCorect.toString()

            // Animatie pe text
            val scaleX = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.3f, 1f)
            val scaleY = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.3f, 1f)

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
                .collection("progresScaderi")
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