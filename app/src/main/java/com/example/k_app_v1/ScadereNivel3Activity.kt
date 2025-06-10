package com.example.k_app_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ScadereNivel3Activity : AppCompatActivity() {
    private lateinit var numar1Text: TextView
    private lateinit var numar2Text: TextView
    private lateinit var minusText: TextView
    private lateinit var egalText: TextView
    private lateinit var rezultatText: TextView
    private lateinit var raspunsEditText: EditText
    private lateinit var verificaBtn: Button
    private lateinit var feedbackTextView: TextView

    private var exercitiuCurent = 0
    private val totalExercitii = 10
    private var scor = 0
    private var raspunsCorect = 0

    private lateinit var sunetCorect: MediaPlayer
    private lateinit var sunetGresit: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scadere_nivel3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val main = findViewById<ScrollView>(R.id.main)
        numar1Text = findViewById(R.id.numar1)
        numar2Text = findViewById(R.id.numar2)
        minusText = findViewById(R.id.plus)
        egalText = findViewById(R.id.egal)
        rezultatText = findViewById(R.id.rezultatBox)
        raspunsEditText = findViewById(R.id.raspunsEditText)
        verificaBtn = findViewById(R.id.verificaBtn)
        feedbackTextView = findViewById(R.id.feedbackTextView)

        raspunsEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                main.postDelayed({
                    main.fullScroll(View.FOCUS_DOWN)
                }, 200)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
            val imeInset = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, imeInset)
            insets
        }

        minusText.text = "-"
        egalText.text = "="

        sunetCorect = MediaPlayer.create(this, R.raw.corect)
        sunetGresit = MediaPlayer.create(this, R.raw.gresit)

        genereazaOperatie()

        verificaBtn.setOnClickListener {
            ascundeTastatura()
            verificaRaspuns()
        }
    }


    private fun genereazaOperatie() {
        if (exercitiuCurent >= totalExercitii) {
            feedbackTextView.text = "Scor final: $scor / $totalExercitii"
            salveazaProgres()
            Handler(Looper.getMainLooper()).postDelayed({ finish() }, 3500)
            return
        }
        exercitiuCurent++
        feedbackTextView.text = ""
        rezultatText.text = "?"
        raspunsEditText.text.clear()

        val a = (40..100).random() // Poți schimba intervalul
        val b = (0..40).random()
        raspunsCorect = a - b

        numar1Text.text = a.toString()
        numar2Text.text = b.toString()
    }

    private fun verificaRaspuns() {
        val userInput = raspunsEditText.text.toString().toIntOrNull()
        if (userInput == null) {
            feedbackTextView.text = "Introdu un număr!"
            return
        }

        if (userInput == raspunsCorect) {
            scor++
            rezultatText.text = raspunsCorect.toString()
            feedbackTextView.text = getString(R.string.mesaj_bravo)
            sunetCorect.start()

            val animatorSet = AnimatorSet()
            val scaleX = ObjectAnimator.ofFloat(raspunsEditText, "scaleX", 1f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(raspunsEditText, "scaleY", 1f, 1.2f, 1f)
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 600
            animatorSet.start()
        } else {
            feedbackTextView.text = getString(R.string.mesaj_gresit)
            sunetGresit.start()


            val shake = ObjectAnimator.ofFloat(raspunsEditText, "translationX", 0f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
            shake.duration = 600
            shake.start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            genereazaOperatie()
        }, 2000)



        /*
        val esteCorect = userInput == rezultatCorect
        feedbackTextView.text = if (esteCorect) "Corect!" else "Greșit!"

        if (esteCorect) scor++

        // Actualizează ? cu rezultatul
        val tv = operatieContainer.getChildAt(4) as TextView
        tv.text = rezultatCorect.toString()

        verificaBtn.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            numarExercitiu++
            if (numarExercitiu < totalExercitii) {
                verificaBtn.isEnabled = true
                genereazaOperatie()
            } else {
                feedbackTextView.text = "Ai terminat! Scor: $scor din $totalExercitii"
                verificaBtn.visibility = View.GONE
            }
        }, 2000)
         */
    }

    private fun ascundeTastatura() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(raspunsEditText.windowToken, 0)
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
                .document("nivel3")
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

    // Extensie pentru dp în pixeli
    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}