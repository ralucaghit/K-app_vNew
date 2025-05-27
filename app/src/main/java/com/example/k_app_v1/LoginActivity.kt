package com.example.k_app_v1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inițializează Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Dacă utilizatorul este deja logat, sari peste logare
        if (auth.currentUser != null) {
            goToMainActivity()
        }

        emailEditText = findViewById(R.id.email_input)
        passwordEditText = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_btn)
        signupButton = findViewById(R.id.signup_btn)

        loginButton.setOnClickListener {
            loginUser()
        }

        signupButton.setOnClickListener {
            goToSignupActivity()
        }

    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish() // opțional, ca să nu poată reveni cu back
    }

    private fun goToSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish() // opțional, ca să nu poată reveni cu back
    }

        /*override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_login)

            val scrollView = findViewById<androidx.core.widget.NestedScrollView>(R.id.main)
            val usernameInput = findViewById<EditText>(R.id.username_input)
            val passwordInput = findViewById<EditText>(R.id.password_input)

            usernameInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) scrollView.scrollTo(0, usernameInput.bottom)
            }

            passwordInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) scrollView.scrollTo(0, passwordInput.bottom)
            }
        }

        fun clicked (view: View) {
            // get views
            val eUser = findViewById<EditText>(R.id.username_input)
            val ePass = findViewById<EditText>(R.id.password_input)

            // Obține textul și elimină spațiile goale
            val user = eUser.text.toString().trim()
            val pass = ePass.text.toString().trim()

            // verify that both fields are filled
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vă rog introduceți atât numele de utilizator, cât și parola!", Toast.LENGTH_LONG).show()
                return
            }
            doLogin(user, pass);
        }

        private fun doLogin(user: String, pass: String) {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.putExtra("username", user)
            intent.putExtra("password", pass)
            startActivity(intent)
        }*/
}