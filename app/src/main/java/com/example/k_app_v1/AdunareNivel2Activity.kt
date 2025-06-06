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
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.graphics.toColorInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdunareNivel2Activity : AppCompatActivity() {
    private lateinit var numar1Text: TextView
    private lateinit var numar2Text: TextView
    private lateinit var plusText: TextView
    private lateinit var egalText: TextView
    private lateinit var rezultatText: TextView
    private lateinit var butoaneGrid: GridLayout
    private lateinit var feedbackText: TextView

    private var exercitiuCurent = 0
    private val totalExercitii = 2
    private var scor = 0
    private var raspunsCorect = 0

    private lateinit var sunetCorect: MediaPlayer
    private lateinit var sunetGresit: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adunare_nivel2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        numar1Text = findViewById(R.id.numar1)
        numar2Text = findViewById(R.id.numar2)
        plusText = findViewById(R.id.plus)
        egalText = findViewById(R.id.egal)
        rezultatText = findViewById(R.id.rezultatBox)
        butoaneGrid = findViewById(R.id.gridNumere)
        feedbackText = findViewById(R.id.feedbackText)

        plusText.text = "+"
        egalText.text = "="

        sunetCorect = MediaPlayer.create(this, R.raw.corect)
        sunetGresit = MediaPlayer.create(this, R.raw.gresit)

        genereazaExercitiu()
    }

    private fun genereazaExercitiu() {
        if (exercitiuCurent >= totalExercitii) {
            feedbackText.text = "Scor final: $scor / $totalExercitii"
            salveazaProgres()
            Handler(Looper.getMainLooper()).postDelayed({ finish() }, 3500)
            return
        }

        exercitiuCurent++
        feedbackText.text = ""
        rezultatText.text = "?"

        val n1 = (0..10).random()
        val n2 = (1..30).random()
        raspunsCorect = n1 + n2

        numar1Text.text = n1.toString()
        numar2Text.text = n2.toString()

        val variante = mutableSetOf<Int>()
        variante.add(raspunsCorect)
        while (variante.size < 4) {
            variante.add((0..30).random())//raspunsCorect - 5..raspunsCorect + 5).random())
        }

        val colors = listOf("#FFF9C4", "#F8BBD0", "#F8DAC5", "#B2EBF2")
        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

        butoaneGrid.removeAllViews()

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
            scor++
            rezultatText.text = raspunsCorect.toString()
            feedbackText.text = getString(R.string.mesaj_bravo)
            sunetCorect.start()

            val animatorSet = AnimatorSet()
            val scaleX = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.2f, 1f)
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 600
            animatorSet.start()
        } else {
            feedbackText.text = getString(R.string.mesaj_gresit)
            sunetGresit.start()

            val shake = ObjectAnimator.ofFloat(btn, "translationX", 0f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
            shake.duration = 600
            shake.start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            genereazaExercitiu()
        }, 2000)
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
                .collection("progresAdunari")
                .document("nivel2")
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
        sunetCorect.release()
        sunetGresit.release()
    }
}