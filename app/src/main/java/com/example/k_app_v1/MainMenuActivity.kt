package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

fun View.addPressEffect(alphaPressed: Float = 0.7f) {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.alpha = alphaPressed
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.alpha = 1f
                if (event.action == MotionEvent.ACTION_UP) v.performClick()
                true
            }
            else -> false
        }
    }
}

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        val romanaButton: CardView = findViewById(R.id.romana_btn)
        val mateButton: CardView = findViewById(R.id.math_btn)
        val progressButton: Button = findViewById(R.id.progresBtn)

        romanaButton.addPressEffect()
        mateButton.addPressEffect()

        romanaButton.setOnClickListener {
            romanaButton.isClickable = false
            romanaButton.postDelayed({
                val intent = Intent(this, LimbaRomanaActivity::class.java)
                startActivity(intent)
                romanaButton.isClickable = true
            }, 130)
        }


        mateButton.setOnClickListener {
            mateButton.isClickable = false
            mateButton.postDelayed({
                val intent = Intent(this, MateActivity::class.java)
                startActivity(intent)
                mateButton.isClickable = true
            }, 130)
        }

        progressButton.setOnClickListener{
            val intent = Intent(this, ProgresActivity::class.java)
            startActivity(intent)
        }
    }
}