package com.example.k_app_v1

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ExMissingLetterActivity : AppCompatActivity() {
    private lateinit var imagineView: ImageView
    private lateinit var cuvantTextView: TextView
    private lateinit var gridLitere: GridLayout
    private lateinit var mesajTextView: TextView

    private var exercitii = mutableListOf<Map<String, Any>>()
    private var indexCurent = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ex_missing_letter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        imagineView = findViewById(R.id.imagineView)
        cuvantTextView = findViewById(R.id.cuvantTextView)
        gridLitere = findViewById(R.id.gridLitere)
        mesajTextView = findViewById(R.id.mesajTextView)

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("LimbaRomana").document("romana")
            .collection("exercitiiLiteraLipsa")
            .orderBy("index")
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
            .addOnFailureListener { e ->
                Log.e("FIREBASE_ERROR", "Eroare la preluare: ${e.message}", e)
                Toast.makeText(this, "Eroare la încărcare exerciții.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun afiseazaExercitiu() {
        if (indexCurent >= exercitii.size) {
            mesajTextView.text = "Felicitări!\nAi terminat toate exercițiile! 🎉"
            gridLitere.removeAllViews()

            Handler(Looper.getMainLooper()).postDelayed({
                finish()  // Închide activitatea după 2 secunde
            }, 2500)

            return

        }

        val exercitiu = exercitii[indexCurent]
        val imagineUrl = exercitiu["imagine"] as? String ?: ""
        val cuvant = exercitiu["cuvant"] as? String ?: ""
        val indexLipsa = (exercitiu["indexLiteraLipsa"] as? Long)?.toInt() ?: 0
        val variante = exercitiu["variante"] as? List<*> ?: emptyList<Any>()
        val literaCorecta = cuvant.getOrNull(indexLipsa)?.toString() ?: ""
        val colors = listOf("#FFF9C4", "#F8BBD0", "#E1BEE7", "#B2EBF2")

        // Generăm cuvântul cu lipsa
        val cuvantCuLipsa = cuvant.replaceRange(indexLipsa, indexLipsa + 1, "_")
        cuvantTextView.text = cuvantCuLipsa
        mesajTextView.text = ""

        Glide.with(this).load(imagineUrl).into(imagineView)
        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

        gridLitere.removeAllViews()

        for ((index, varianta) in variante.withIndex()) {
            if (varianta is String) {
                val btn = Button(this).apply {
                    text = varianta
                    setAllCaps(false)
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    textSize = 40f

                    // Asignează culoarea de pe poziția index
                    if (index < colors.size) {
                        background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
                        background?.setTint(Color.parseColor(colors[index]))
                    }

                    setTextColor(Color.DKGRAY)
                    typeface?.let { this.typeface = it }

                    // ✅ Margin și padding pentru aspect mai frumos

                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 200
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(30, 30, 30, 30)  // stânga, sus, dreapta, jos
                    }

                    setPadding(20, 20, 20, 20)

                    setOnClickListener {
                        verificaRaspuns(varianta, literaCorecta, this)
                    }
                }
                gridLitere.addView(btn)
            }
        }
    }

    private fun verificaRaspuns(raspuns: String, corect: String, btn: Button) {
        if (raspuns == corect) {
            mesajTextView.text = "Bravo! ✅"

            // ✅ Animatie pe buton
            val scaleX = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.2f, 1f)

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 550
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()

            // După o scurtă întârziere trecem mai departe
            Handler(Looper.getMainLooper()).postDelayed({
                indexCurent++
                afiseazaExercitiu()
            }, 2000)

        } else {
            mesajTextView.text = "Încearcă din nou! ❌"
        }
    }
}