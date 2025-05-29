package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LimbaRomanaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_limba_romana)

        val allLettersButton: Button = findViewById(R.id.allLetters_btn)
        val missingLetterButton: Button = findViewById(R.id.missingLetter_btn)

        allLettersButton.setOnClickListener{
            val intent = Intent(this, AllLettersActivity::class.java)
            startActivity(intent)
        }

        missingLetterButton.setOnClickListener{
            val intent = Intent(this, ExMissingLetterActivity::class.java)
            startActivity(intent)
        }
    }
}