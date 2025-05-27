package com.example.k_app_v1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AllLettersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_letters)

        val grid = findViewById<GridLayout>(R.id.gridLitere)

        val typeface = ResourcesCompat.getFont(this, R.font.andika)

        ViewCompat.setOnApplyWindowInsetsListener(grid) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                bottomInset + 82  // adaugăm un mic spațiu în plus
            )
            insets
        }

        //val litere = ('a'..'z').toList()
        val litere = listOf('a', 'm', 'i', 'n', 'e', 'u', 'r', 'o', 'c', 'ă', 'l', 't', 's', 'p', 'v', 'd', 'ș', 'î', 'â', 'b', 'j', 'h', 'g', 'ț', 'z', 'f', 'x', 'k', 'q', 'w', 'y')

        val colors = listOf("#FFCDD0", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#BBDEFB", "#B2EBF2", "#C8E6C9", "#FFF9C4", "#FFE0B2")

        for ((index, litera) in litere.withIndex()) {
            val btn = Button(this).apply {
                text = litera.toString()
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 68f)

                background = getDrawable(R.drawable.rounded_button_background)
                background?.setTint(Color.parseColor(colors[index % colors.size]))
                setTextColor(Color.DKGRAY)

                setAllCaps(false)
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
                    val intent = Intent(this@AllLettersActivity, CardsForSpecificLetterActivity::class.java)
                    intent.putExtra("LiteraId", "Litera${litera.uppercase()}")
                    startActivity(intent)
                }
            }

            grid.addView(btn)
        }
    }
}