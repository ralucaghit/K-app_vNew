package com.example.k_app_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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

class CrescDescrescActivity : AppCompatActivity() {
    private lateinit var instructiuneTextView: TextView
    private lateinit var slotContainer: LinearLayout
    private lateinit var numereContainer: LinearLayout
    private lateinit var verificaBtn: Button
    private lateinit var rezultatTextView: TextView
    private lateinit var corectSound: MediaPlayer
    private lateinit var gresitSound: MediaPlayer

    private var scorFinal = 0
    private var numarExercitiu = 1
    private val totalExercitii = 10
    private var numereCorecte: List<Int> = listOf()
    private val sloturi: MutableList<TextView> = mutableListOf()
    private val numereJos: MutableList<Button> = mutableListOf()
    private var esteCrescator = true

    private val colors = listOf("#FFF9C4", "#F8BBD0", "#B2EBF2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cresc_descresc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        instructiuneTextView = findViewById(R.id.instructiuneTextView)
        slotContainer = findViewById(R.id.slotContainer)
        numereContainer = findViewById(R.id.numereContainer)
        verificaBtn = findViewById(R.id.verificaBtn)
        rezultatTextView = findViewById(R.id.rezultatTextView)
        corectSound = MediaPlayer.create(this, R.raw.corect)
        gresitSound = MediaPlayer.create(this, R.raw.gresit)

        genereazaExercitiu()

        verificaBtn.setOnClickListener {
            verificaRaspuns()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun genereazaExercitiu() {
        // Golim UI-ul
        slotContainer.removeAllViews()
        numereContainer.removeAllViews()
        sloturi.clear()
        numereJos.clear()
        rezultatTextView.text = ""

        numereContainer.setOnDragListener(numereContainerDropListener)

        // Stabilim dacă e crescător sau descrescător
        esteCrescator = (0..1).random() == 0
        instructiuneTextView.text = if (esteCrescator)
            "Pune numerele în ordine crescătoare!"
        else
            "Pune numerele în ordine descrescătoare!"

        // Generăm lista de 5 numere
        val start = (0..8).random()
        val pas = 1
        numereCorecte = (0..3).map { start + it * pas }
        if (!esteCrescator) numereCorecte = numereCorecte.reversed()

        val pozitiiFixate = (0..3).shuffled().take(1).sorted()


        // Creăm sloturile
        for (i in 0..3) {
            val slot = object : androidx.appcompat.widget.AppCompatTextView(this) {
                override fun performClick(): Boolean {
                    super.performClick()
                    return true
                }
            }

            slot.layoutParams = LinearLayout.LayoutParams(110, 140).apply {
                weight = 1f
                marginStart = 8
                marginEnd = 8
            }
            val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)
            slot.textSize = 25f
            typeface?.let { slot.typeface = it }
            slot.gravity = Gravity.CENTER
            slot.setBackgroundResource(R.drawable.slot_background)

            if (pozitiiFixate.contains(i)) {
                slot.text = numereCorecte[i].toString()
                slot.tag = "fix"
            } else {
                slot.setOnDragListener(dragListener)
                slot.text = ""
                slot.tag = "empty"

                slot.setOnTouchListener { _, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_DOWN && slot.tag == "filled") {
                        val text = slot.text.toString()
                        val color = slot.getTag(R.id.culoare_tag) as? String ?: "#E09A54"
                        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

                        val btn = Button(this).apply {
                            this.text = text
                            textSize = 23f
                            background = ContextCompat.getDrawable(this@CrescDescrescActivity, R.drawable.rounded_button_background)
                            background?.setTint(color.toColorInt())
                            tag = color
                            setTag(R.id.culoare_tag, color)
                            setTextColor(Color.DKGRAY)
                            setPadding(120,35,120,35)
                            typeface?.let { this.typeface = it }

                            setOnTouchListener { bView, bEvent ->
                                if (bEvent.action == MotionEvent.ACTION_DOWN) {
                                    val data = ClipData.newPlainText("number", text)
                                    val shadow = View.DragShadowBuilder(bView)
                                    bView.startDragAndDrop(data, shadow, bView, 0)
                                    true
                                } else false
                            }
                        }
                        val layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginStart = 10
                            marginEnd = 10
                            topMargin = 10
                            bottomMargin = 10
                        }
                        btn.layoutParams = layoutParams

                        numereContainer.addView(btn)
                        slot.text = ""
                        slot.tag = "empty"
                        slot.setTag(R.id.culoare_tag, null)

                        true
                    } else false
                }
            }

            sloturi.add(slot)
            slotContainer.addView(slot)
        }

        // Creăm butoanele pentru numerele rămase
        val restante = numereCorecte
            .mapIndexed { index, valoare -> index to valoare }
            .filter { (index, _) -> !pozitiiFixate.contains(index) }
            .map { it.second }
            .shuffled()

        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

        restante.forEachIndexed { i, numar ->
            val btn = Button(this).apply {
                text = numar.toString()
                textSize = 23f
                background = ContextCompat.getDrawable(this@CrescDescrescActivity, R.drawable.rounded_button_background)
                val color = colors[i % colors.size]
                background?.setTint(color.toColorInt())
                tag = color
                setTag(R.id.culoare_tag, color)
                setTextColor(Color.DKGRAY)
                setPadding(120,35,120,35)
                typeface?.let { this.typeface = it }

                setOnTouchListener { view, _ ->
                    view.performClick()
                    val data = ClipData.newPlainText("number", text)
                    val shadow = View.DragShadowBuilder(view)
                    view.startDragAndDrop(data, shadow, view, 0)
                    true
                }
            }
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 10
                marginEnd = 10
                topMargin = 10
                bottomMargin = 10
            }
            btn.layoutParams = layoutParams

            numereJos.add(btn)
            numereContainer.addView(btn)
        }
    }


    private val dragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                val draggedBtn = event.localState as Button
                val dest = v as TextView

                // Dacă slotul are deja un număr, îl scoatem înapoi jos
                if (dest.tag != "fix" && dest.text.isNotEmpty()) {
                    val btnText = dest.text.toString()

                    // Luăm culoarea originală din tag
                    val originalColor = dest.getTag(R.id.culoare_tag) as? String ?: "#E09A54"
                    val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre_bold)

                    val btn = Button(this).apply {
                        text = btnText
                        textSize = 23f
                        background = ContextCompat.getDrawable(this@CrescDescrescActivity, R.drawable.rounded_button_background)
                        background?.setTint(originalColor.toColorInt())
                        tag = originalColor
                        setTag(R.id.culoare_tag, originalColor)
                        setTextColor(Color.DKGRAY)
                        setPadding(120,35,120,35)
                        typeface?.let { this.typeface = it }

                        setOnTouchListener { view, _ ->
                            view.performClick()
                            val data = ClipData.newPlainText("number", text)
                            val shadow = View.DragShadowBuilder(view)
                            view.startDragAndDrop(data, shadow, view, 0)
                            true
                        }
                    }

                    val layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = 10
                        marginEnd = 10
                        topMargin = 10
                        bottomMargin = 10
                    }
                    btn.layoutParams = layoutParams

                    numereContainer.addView(btn)
                }

                // Pune butonul în slot
                if (dest.tag != "fix") {
                    val draggedColor = draggedBtn.tag as? String ?: "#E09A54"

                    dest.text = draggedBtn.text
                    dest.tag = "filled"
                    dest.setTag(R.id.culoare_tag, draggedColor)

                    (draggedBtn.parent as? ViewGroup)?.removeView(draggedBtn)
                }

                true
            }

            else -> true
        }
    }

    private val numereContainerDropListener = View.OnDragListener { _, event ->
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                val draggedBtn = event.localState as Button
                val parent = draggedBtn.parent as? ViewGroup
                parent?.removeView(draggedBtn)

                // Evită duplicarea
                if (draggedBtn.parent != numereContainer) {
                    numereContainer.addView(draggedBtn)
                }
                true
            }
            else -> true
        }
    }

    private fun verificaRaspuns() {
        val raspuns = sloturi.map {
            try {
                it.text.toString().toInt()
            } catch (e: Exception) {
                return@map null
            }
        }

        if (raspuns.contains(null)) {
            rezultatTextView.text = "Completează toate spațiile!"
            rezultatTextView.setTextColor(Color.RED)
            return
        }

        val corect = raspuns.filterNotNull() == numereCorecte

        rezultatTextView.text =
            if (corect) getString(R.string.mesaj_bravo) else getString(R.string.mesaj_gresit)
        rezultatTextView.setTextColor(if (corect) "#4CAF50".toColorInt() else Color.RED)
        if (corect) {
            scorFinal++
            corectSound.start()

            val animatorSet = AnimatorSet()
            val scaleX = ObjectAnimator.ofFloat(slotContainer, "scaleX", 1f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(slotContainer, "scaleY", 1f, 1.2f, 1f)
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.duration = 600
            animatorSet.start()

        } else {
            gresitSound.start()

            val shake = ObjectAnimator.ofFloat(slotContainer, "translationX", 0f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
            shake.duration = 600
            shake.start()
        }

        verificaBtn.isEnabled = false // prevenim dublu click


        if (numarExercitiu < totalExercitii) {
            verificaBtn.isEnabled = true

            Handler(Looper.getMainLooper()).postDelayed({
                numarExercitiu++
                genereazaExercitiu()
            }, 2000)
        } else {
            rezultatTextView.text =
                "Felicitări! Ai făcut $scorFinal din $totalExercitii corect!"
            rezultatTextView.setTextColor("#4CAF50".toColorInt())
            verificaBtn.visibility = View.GONE
            salveazaProgres()
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 3000)
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
            .collection("progresCrescDescresc")
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