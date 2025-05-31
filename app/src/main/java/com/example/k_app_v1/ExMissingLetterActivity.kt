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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ExMissingLetterActivity : AppCompatActivity() {
    private lateinit var imagineView: ImageView
    private lateinit var cuvantTextView: TextView
    private lateinit var gridLitere: GridLayout
    private lateinit var mesajTextView: TextView

    private var categorie = 0

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

        // Preluare index categorie din Intent
        categorie = intent.getIntExtra("Categorie", 0)

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("LimbaRomana").document("romana")
            .collection("exercitiiLiteraLipsa")
            .orderBy("index")
            .get()
            .addOnSuccessListener { result ->
                val allExercitii = mutableListOf<Map<String, Any>>()
                for (document in result) {
                    allExercitii.add(document.data)
                }

                var startIndex = 0
                var endIndex = 0

                // Selectăm doar setul dorit (8 exerciții per categorie)
                if(categorie == 0 || categorie == 1 || categorie == 2 || categorie == 3 || categorie == 4 || categorie == 5 ) {
                    startIndex = categorie * 8
                    endIndex = minOf(startIndex + 8, allExercitii.size)
                } else{
                    if(categorie == 6) {
                        startIndex = 48
                        endIndex = 54
                    } else {
                        if(categorie == 7) {
                            startIndex = 54
                            endIndex = 61
                        }
                    }
                }

                exercitii = allExercitii.subList(startIndex, endIndex).toMutableList()

                if (exercitii.isNotEmpty()) {
                    afiseazaExercitiu()
                } else {
                    Toast.makeText(this, "Nu s-au găsit exerciții pentru această categorie.", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_ERROR", "Eroare la preluare: ${e.message}", e)
                Toast.makeText(this, "Eroare la încărcare exerciții.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun afiseazaExercitiu() {
        if (indexCurent >= exercitii.size) {
            mesajTextView.text = getString(R.string.mesaj_felicitari)
            gridLitere.removeAllViews()

            salveazaProgres()

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

                    // Margin și padding pentru aspect mai frumos

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
            mesajTextView.text = getString(R.string.mesaj_bravo)

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
            mesajTextView.text = getString(R.string.mesaj_mai_incearca)
        }
    }

    private fun salveazaProgres() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val documentPath = "Categorie$categorie"
        if (userId != null) {
            val progres = hashMapOf(
                "terminat" to true,
                "data" to FieldValue.serverTimestamp()
            )

            FirebaseFirestore.getInstance()
                .collection("copii")
                .document(userId)
                .collection("progresExercitiiLiteraLipsa")
                .document(documentPath)
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