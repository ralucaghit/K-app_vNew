package com.example.k_app_v1

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdunareNivel1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adunare_nivel1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                .collection("progresAdunari")
                .document("nivel1")
                .set(progres)
                .addOnSuccessListener {
                    Log.d("FIREBASE_SAVE", "Progres salvat!")
                }

                .addOnFailureListener { e ->
                    Log.e("FIREBASE_SAVE", "Eroare la salvarea progresului: ${e.message}", e)
                }
        }
    }
}