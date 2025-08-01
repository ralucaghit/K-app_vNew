package com.example.k_app_v1

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CardsForDigitsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var terminatButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cards_for_digits)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        terminatButton = findViewById(R.id.terminatButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        terminatButton.setOnClickListener {
            salveazaProgres()
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        incarcaDateCifre()
    }

    private fun incarcaDateCifre() {
        FirebaseFirestore.getInstance()
            .collection("Matematica").document("matematica")
            .collection("cifre").document("imagini")
            .get()
            .addOnSuccessListener { doc ->
                Log.d("FIREBASE_STATUS", "Am primit documentul.")

                val rawList = doc.get("imagini")
                val listaImagini = if (rawList is List<*>) rawList.filterIsInstance<String>() else emptyList()

                recyclerView.adapter = ImaginiAdapter(listaImagini)
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_ERROR", "Eroare la preluare: ${e.message}", e)
            }
    }

    private fun salveazaProgres() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val progres = hashMapOf(
                "terminat" to true,
                "data" to FieldValue.serverTimestamp()
            )

            FirebaseFirestore.getInstance()
                .collection("copii")
                .document(userId)
                .collection("progresCifre")
                .document("finalizat")
                .set(progres)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.d("FIREBASE_SAVE", "Eroare la salvare: ${e.message}")
                }
        }
    }
}