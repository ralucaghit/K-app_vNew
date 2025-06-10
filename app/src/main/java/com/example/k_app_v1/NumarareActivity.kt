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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NumarareActivity : AppCompatActivity() {
    private lateinit var imagineView: ImageView
    private lateinit var enuntTextView: TextView
    private lateinit var gridRaspunsuri: GridLayout
    private lateinit var mesajTextView: TextView
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var exercitii = mutableListOf<Map<String, Any>>()
    private var indexCurent = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_numarare)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagineView = findViewById(R.id.imagineView)
        enuntTextView = findViewById(R.id.enuntTextView)
        gridRaspunsuri = findViewById(R.id.gridRaspunsuri)
        mesajTextView = findViewById(R.id.mesajTextView)

        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("Matematica")
            .document("matematica")
            .collection("numarare")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    exercitii.add(document.data)
                }
                if (exercitii.isNotEmpty()) {
                    afiseazaExercitiu()
                } else {
                    Toast.makeText(this, "Niciun exercițiu găsit!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Eroare la încărcare: ${e.message}", e)
            }
    }

    private fun afiseazaExercitiu() {
        if (indexCurent >= exercitii.size) {
            mesajTextView.text = getString(R.string.mesaj_felicitari)
            gridRaspunsuri.removeAllViews()

            salveazaProgres()

            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 2500)
            return
        }

        val exercitiu = exercitii[indexCurent]
        val imagineUrl = exercitiu["imagine"] as? String ?: ""
        val raspunsCorect = (exercitiu["raspuns"] as? Long)?.toInt() ?: 0
        //val textAjutator = exercitiu["textAjutator"] as? String ?: "Câte obiecte vezi?"

        //enuntTextView.text = textAjutator
        Glide.with(this).load(imagineUrl).into(imagineView)
        mesajTextView.text = ""

        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)
        val colors = listOf("#FFCDD0", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#BBDEFB", "#B2EBF2", "#C8E6C9", "#FFF9C4", "#FFE0B2", "#FFCDD0", "#B1E9DC")


        gridRaspunsuri.removeAllViews()

        for (i in 1..10) {
            val btn = Button(this).apply {
                text = i.toString()
                textSize = 20f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER

                // Asignează culoarea de pe poziția index
                if (i < colors.size) {
                    background = ContextCompat.getDrawable(context, R.drawable.rounded_button_short)
                    background?.setTint(Color.parseColor(colors[i]))
                }

                typeface?.let { this.typeface = it }

                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 180
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(15, 15, 15, 15)
                }
                //setPadding(15, 15, 15, 15)

                setOnClickListener {
                    verificaRaspuns(i, raspunsCorect, this)
                }
            }
            gridRaspunsuri.addView(btn)
        }
    }

    private fun verificaRaspuns(ales: Int, corect: Int, btn: Button) {
        if (ales == corect) {
            corectSound.start()
            mesajTextView.text = getString(R.string.mesaj_bravo)

            val animatorSet = AnimatorSet()
            val scaleX = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.2f, 1f)
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 600
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val progres = hashMapOf(
            "terminat" to true,
            "data" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance()
            .collection("copii")
            .document(userId)
            .collection("progresNumarare")
            .document("finalizat")
            .set(progres)
            .addOnSuccessListener { Log.d("FIREBASE_SAVE", "Progres salvat!") }
            .addOnFailureListener { e -> Log.e("FIREBASE_SAVE", "Eroare salvare: ${e.message}") }
    }

    override fun onDestroy() {
        super.onDestroy()
        corectSound.release()
        gresitSound.release()
    }
}