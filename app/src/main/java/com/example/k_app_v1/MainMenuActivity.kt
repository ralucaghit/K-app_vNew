package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        val romanaButton: CardView = findViewById(R.id.romana_btn)
        val mateButton: CardView = findViewById(R.id.math_btn)
        val progressButton: Button = findViewById(R.id.progresBtn)

        romanaButton.setOnClickListener{
            val intent = Intent(this, LimbaRomanaActivity::class.java)
            startActivity(intent)
        }

        mateButton.setOnClickListener{
            val intent = Intent(this, MateActivity::class.java)
            startActivity(intent)
        }

        progressButton.setOnClickListener{
            val intent = Intent(this, ProgresActivity::class.java)
            startActivity(intent)
        }
    }
}