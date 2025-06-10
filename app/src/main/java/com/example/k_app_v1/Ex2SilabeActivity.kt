package com.example.k_app_v1

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
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
import kotlin.random.Random

class Ex2SilabeActivity : AppCompatActivity() {
    private lateinit var imagineView: ImageView
    private lateinit var slotContainer: LinearLayout
    private lateinit var silabeGrid: GridLayout
    private lateinit var mesajTextView: TextView
    private lateinit var verificaBtn: Button
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var exercitii = mutableListOf<Map<String, Any>>()
    private var indexCurent = 0
    private var sloturi = mutableListOf<TextView>()
    private var ordineAleasa = mutableListOf<String>()
    private var butoaneSilabe = mutableMapOf<String, Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ex2_silabe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagineView = findViewById(R.id.imagineView)
        slotContainer = findViewById(R.id.slotContainer)
        silabeGrid = findViewById(R.id.silabeGrid)
        mesajTextView = findViewById(R.id.mesajTextView)
        verificaBtn = findViewById(R.id.verificaBtn)

        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        verificaBtn.setOnClickListener { verificaRaspuns() }

        incarcaExercitii()
    }

    private fun incarcaExercitii() {
        FirebaseFirestore.getInstance()
            .collection("LimbaRomana")
            .document("romana")
            .collection("exercitiiSilabe2")
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
            slotContainer.removeAllViews()
            silabeGrid.removeAllViews()
            verificaBtn.clearFocus()

            salveazaProgres()
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 3000)
            return
        }

        val exercitiu = exercitii[indexCurent]
        val imagineUrl = exercitiu["imagine"] as? String ?: ""
        val silabe = (exercitiu["silabe"] as? List<*>)?.map { it.toString() } ?: listOf()

        Glide.with(this).load(imagineUrl).into(imagineView)
        mesajTextView.text = ""
        ordineAleasa.clear()
        butoaneSilabe.clear()
        sloturi.clear()
        slotContainer.removeAllViews()
        silabeGrid.removeAllViews()

        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre)
        val colors = listOf("#FFF9C4", "#F8BBD0", "#E1BEE7", "#B2EBF2")

        // creează sloturi vizuale
        for (i in silabe.indices) {
            val slot = TextView(this).apply {
                text = "____"
                textSize = 24f
                gravity = Gravity.CENTER
                isAllCaps = false

                setPadding(16, 8, 16, 8)
                setBackgroundResource(R.drawable.slot_background)
            }
            slotContainer.addView(slot)
            sloturi.add(slot)
            if (i < silabe.size - 1) {
                val dash = TextView(this).apply {
                    text = "-"
                    textSize = 24f
                    setPadding(4, 0, 4, 0)
                }
                slotContainer.addView(dash)
            }
        }


        // creează butoane silabe random
        val silabeRandom = silabe.shuffled()
        for ((index, silaba) in silabeRandom.withIndex()) {
            val btn = Button(this).apply {
                text = silaba
                textSize = 25f
                isAllCaps = false

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
                    setMargins(10, 10, 10, 10)  // stânga, sus, dreapta, jos
                }

                setPadding(10, 10, 10, 10)

                setOnClickListener { adaugaInSlot(silaba, this) }
            }
            butoaneSilabe[silaba] = btn
            silabeGrid.addView(btn)
        }
    }

    private fun adaugaInSlot(silaba: String, btn: Button) {
        if (ordineAleasa.size < sloturi.size) {
            val indexSlot = ordineAleasa.size
            sloturi[indexSlot].text = silaba
            ordineAleasa.add(silaba)
            btn.isEnabled = false

            sloturi[indexSlot].setOnClickListener {
                // dacă apasă pe slot, scoate silaba înapoi
                // verificăm dacă slotul are efectiv o silabă plasată
                if (indexSlot < ordineAleasa.size && sloturi[indexSlot].text != "____") {
                    val silabaEliminata = ordineAleasa[indexSlot]
                    ordineAleasa.removeAt(indexSlot)
                    sloturi[indexSlot].text = "____"
                    butoaneSilabe[silabaEliminata]?.isEnabled = true
                    rearanjeazaSloturi()
                }
            }
        }
    }

    private fun rearanjeazaSloturi() {
        for (i in sloturi.indices) {
            sloturi[i].text = if (i < ordineAleasa.size) ordineAleasa[i] else "____"
        }
    }

    private fun verificaRaspuns() {
        val exercitiu = exercitii[indexCurent]
        val silabeCorecte = (exercitiu["silabe"] as? List<*>)?.map { it.toString() } ?: listOf()

        if (ordineAleasa == silabeCorecte) {
            corectSound.start()
            mesajTextView.text = getString(R.string.mesaj_bravo)
            animatieBravo()

            Handler(Looper.getMainLooper()).postDelayed({
                indexCurent++
                afiseazaExercitiu()
            }, 2000)

        } else {
            gresitSound.start()
            val shake = ObjectAnimator.ofFloat(slotContainer, "translationX", 0f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
            shake.duration = 600
            shake.start()

            mesajTextView.text = getString(R.string.mesaj_mai_incearca)
        }
    }

    private fun animatieBravo() {
        val animatorSet = AnimatorSet()
        val scaleX = ObjectAnimator.ofFloat(slotContainer, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(slotContainer, "scaleY", 1f, 1.2f, 1f)
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 600
        animatorSet.start()
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
            .collection("progresExercitiiSilabe2")
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