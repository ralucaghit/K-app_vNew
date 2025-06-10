package com.example.k_app_v1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.graphics.toColorInt

class AllMissingLetterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_missing_letter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val grid = findViewById<GridLayout>(R.id.gridChooseCat)
        val inapoiButton = findViewById<ImageButton>(R.id.backButton)
        val typeface = ResourcesCompat.getFont(this, R.font.averia_sans_libre)

        inapoiButton.setOnClickListener{
            finish()
        }

        val litere = listOf("A/M/I/N", "E/U/R/O", "C/Ă/L/T", "S/P/V/D", "Ș/Î/Â/B", "J/H/G/Ț", "Z/F/X", "K/Q/W/Y")

        val colors = listOf("#FFCDD0", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#BBDEFB", "#B2EBF2", "#C8E6C9", "#FFF9C4", "#FFE0B2")

        for ((index, litera) in litere.withIndex()) {
            val btn = Button(this).apply {
                text = litera
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)

                background = getDrawable(R.drawable.rounded_button_background)
                background?.setTint(colors[index % colors.size].toColorInt())
                setTextColor(Color.DKGRAY)

                isAllCaps = false
                gravity = Gravity.CENTER
                includeFontPadding = false
                setPadding(0, 0, 0, 0)

                typeface?.let { this.typeface = it }

                // Dimensiuni pătrate
                val totalPadding = (16.5 * 3) * resources.displayMetrics.density // 16dp între col, stânga și dreapta
                val size = ((resources.displayMetrics.widthPixels - totalPadding) / 2).toInt()

                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    marginEnd = 38
                    bottomMargin = 30
                }

                setOnClickListener {
                    val intent = Intent(this@AllMissingLetterActivity, ExMissingLetterActivity::class.java)
                    intent.putExtra("Categorie", index)
                    startActivity(intent)
                }
            }

            grid.addView(btn)
        }
    }
}