package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val invatareNumereButton = findViewById<CardView>(R.id.invatam_numere_btn)
        val numarareButton = findViewById<CardView>(R.id.numarare_btn)
        val crescDescrescButton = findViewById<CardView>(R.id.crescator_descrescator_btn)
        val comparareButton = findViewById<CardView>(R.id.comparare_btn)
        val adunareButton = findViewById<CardView>(R.id.adunare_btn)
        val scadereButton = findViewById<CardView>(R.id.scadere_btn)
        val inapoiButton = findViewById<ImageButton>(R.id.backButton)

        invatareNumereButton.setOnClickListener{
            val intent = Intent(this, CardsForDigitsActivity::class.java)
            startActivity(intent)
        }

        numarareButton.setOnClickListener{
            val intent = Intent(this, NumarareActivity::class.java)
            startActivity(intent)
        }

        crescDescrescButton.setOnClickListener{
            val intent = Intent(this, CrescDescrescActivity::class.java)
            startActivity(intent)
        }

        comparareButton.setOnClickListener{
            val intent = Intent(this, ComparareActivity::class.java)
            startActivity(intent)
        }

        adunareButton.setOnClickListener{
            val intent = Intent(this, AdunareActivity::class.java)
            startActivity(intent)
        }

        scadereButton.setOnClickListener{
            val intent = Intent(this, ScadereActivity::class.java)
            startActivity(intent)
        }

        inapoiButton.setOnClickListener{
            finish()
        }
    }
}