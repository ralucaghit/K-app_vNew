package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        val navigateButton: Button = findViewById(R.id.romana_btn)

        navigateButton.setOnClickListener{
            val intent = Intent(this, LimbaRomanaActivity::class.java)
            startActivity(intent)
        }
    }
}