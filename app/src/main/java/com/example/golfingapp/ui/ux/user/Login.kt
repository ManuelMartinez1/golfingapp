package com.example.golfingapp.ui.ux.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.golfingapp.R
import com.example.golfingapp.ui.ux.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogIn : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_golfer)

        usernameEditText = findViewById(R.id.user)
        passwordEditText = findViewById(R.id.passw)
        signInButton = findViewById(R.id.accept2)

        signInButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LogIn, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()
            val usersRef = db.collection("users")

            usersRef.whereEqualTo("usuario", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // No se encontró ningún usuario con el nombre de usuario proporcionado
                        Toast.makeText(this@LogIn, "Nombre de usuario incorrecto", Toast.LENGTH_SHORT).show()
                    } else {
                        // Se encontró un usuario con el nombre de usuario proporcionado
                        val email = documents.documents[0].getString("correo")
                        if (email != null) {
                            signInWithEmailAndPassword(email, password)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@LogIn, "Error al iniciar sesión: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        val mAuth = FirebaseAuth.getInstance()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val intent = Intent(this, HomeFragment::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Inicio de sesión fallido
                    Toast.makeText(this@LogIn, "Error al iniciar sesión: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}