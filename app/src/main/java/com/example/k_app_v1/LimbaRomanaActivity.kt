package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LimbaRomanaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_limba_romana)

        val allLettersButton = findViewById<CardView>(R.id.allLetters_btn)
        val missingLetterButton = findViewById<CardView>(R.id.missingLetter_btn)
        val silabeButton = findViewById<CardView>(R.id.silabe_btn)
        val inapoiButton = findViewById<ImageButton>(R.id.backButton)

        allLettersButton.setOnClickListener{
            val intent = Intent(this, AllLettersActivity::class.java)
            startActivity(intent)
        }

        missingLetterButton.setOnClickListener{
            val intent = Intent(this, AllMissingLetterActivity::class.java)
            startActivity(intent)
        }

        silabeButton.setOnClickListener{
            val intent = Intent(this, AllSilabeActivity::class.java)
            startActivity(intent)
        }

        inapoiButton.setOnClickListener{
            finish()
        }
    }
}