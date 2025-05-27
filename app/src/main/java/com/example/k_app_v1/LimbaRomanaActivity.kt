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

        val navigateButton: Button = findViewById(R.id.allLetters_btn)

        navigateButton.setOnClickListener{
            val intent = Intent(this, AllLettersActivity::class.java)
            startActivity(intent)
        }
    }
}