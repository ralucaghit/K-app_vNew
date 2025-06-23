package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignupActivity : AppCompatActivity() {
    // Declarați obiectele Firebase
    private lateinit var auth: FirebaseAuth

    // Elemente din layout
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var registerButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // Inițializare FirebaseAuth
        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegister)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    // Metodă pentru a înregistra un utilizator
    private fun registerUser() {
        val userEmail = email.text.toString().trim()
        val userPassword = password.text.toString().trim()

        // Verificăm dacă câmpurile sunt completate corect
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }


        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(this, "Registration successful: ${user?.email}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}