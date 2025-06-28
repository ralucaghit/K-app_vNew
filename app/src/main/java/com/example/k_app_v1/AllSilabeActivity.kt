package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AllSilabeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_silabe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val numarSilabeButton = findViewById<CardView>(R.id.numar_silabe_btn)
        val asociereSilabeButton = findViewById<CardView>(R.id.ascociere_silabe_btn)
        val inapoiButton = findViewById<ImageButton>(R.id.backButton)

        numarSilabeButton.addPressEffect()
        asociereSilabeButton.addPressEffect()

        numarSilabeButton.setOnClickListener{
            val intent = Intent(this, Ex1SilabeActivity::class.java)
            startActivity(intent)
        }

        asociereSilabeButton.setOnClickListener{
            val intent = Intent(this, Ex2SilabeActivity::class.java)
            startActivity(intent)
        }

        inapoiButton.setOnClickListener{
            finish()
        }
    }
}