package com.example.k_app_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
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
import androidx.core.graphics.toColorInt

class Ex1SilabeActivity : AppCompatActivity() {
    private lateinit var imagineView: ImageView
    private lateinit var cuvantTextView: TextView
    private lateinit var gridNumere: GridLayout
    private lateinit var mesajTextView: TextView
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var exercitii = mutableListOf<Map<String, Any>>()
    private var indexCurent = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ex1_silabe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagineView = findViewById(R.id.imagineView)
        cuvantTextView = findViewById(R.id.cuvantTextView)
        gridNumere = findViewById(R.id.gridNumere)
        mesajTextView = findViewById(R.id.mesajTextView)
        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("LimbaRomana")
            .document("romana")
            .collection("exercitiiSilabe1")
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
            mesajTextView.text = getString(R.string.mesaj_felicitari)
            gridNumere.removeAllViews()

            salveazaProgres()

            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 2500)
            return
        }

        val exercitiu = exercitii[indexCurent]
        val cuvant = exercitiu["cuvant"] as? String ?: ""
        val imagineUrl = exercitiu["imagine"] as? String ?: ""
        val numarSilabe = (exercitiu["silabe"] as? Long)?.toInt() ?: 0
        val colors = listOf("#FFF9C4", "#F8BBD0", "#E1BEE7", "#B2EBF2", "#FFF9C4")

        cuvantTextView.text = cuvant
        Glide.with(this).load(imagineUrl).into(imagineView)
        mesajTextView.text = ""

        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

        gridNumere.removeAllViews()

        for (i in 1..4) { // presupunem maxim 4 silabe
            val btn = Button(this).apply {
                text = i.toString()
                textSize = 28f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER

                // Asignează culoarea de pe poziția index

                background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
                background?.setTint(colors[i].toColorInt())


                typeface?.let { this.typeface = it }

                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 200
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(30, 30, 30, 30)
                }
                setPadding(20, 20, 20, 20)

                setOnClickListener {
                    verificaRaspuns(i, numarSilabe, this)
                }
            }
            gridNumere.addView(btn)
        }
    }

    private fun verificaRaspuns(raspuns: Int, corect: Int, btn: Button) {
        if (raspuns == corect) {
            corectSound.start()
            mesajTextView.text = getString(R.string.mesaj_bravo)

            // ✅ Animatie pe buton
            val scaleX = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.2f, 1f)

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 550
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()

            Handler(Looper.getMainLooper()).postDelayed({
                indexCurent++
                afiseazaExercitiu()
            }, 2000)
        } else {
            gresitSound.start()
            mesajTextView.text = getString(R.string.mesaj_mai_incearca)
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
                .collection("progresExercitiiSilabe1")
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

    override fun onDestroy() {
        super.onDestroy()
        corectSound.release()
        gresitSound.release()
    }
}