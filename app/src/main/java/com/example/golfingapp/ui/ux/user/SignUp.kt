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

class SignUp : AppCompatActivity() {
    private lateinit var registerButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_golfer)

        // Inicializar vistas
        registerButton = findViewById(R.id.signup_button2)
        emailEditText = findViewById(R.id.email2)
        passwordEditText = findViewById(R.id.password2)
        phoneEditText = findViewById(R.id.phone2)

        registerButton.setOnClickListener {
            // Obtener los valores de los campos de texto
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            // Validar campos obligatorios
            if (email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this@SignUp, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de correo electrónico
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this@SignUp, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar contraseña segura (longitud mínima)
            if (password.length < 6) {
                Toast.makeText(this@SignUp, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamar al método de registro con Firebase Authentication y Firestore
            registrarUsuario(email, password, phone)
        }

        // Aplicar padding para la barra de estado y barra de navegación
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Método para registrar un usuario utilizando Firebase Authentication y guardar los datos en Firestore
    private fun registrarUsuario(email: String, password: String, phone: String) {
        val mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = mAuth.currentUser
                    // Guardar los datos del usuario en Firestore
                    val userData = hashMapOf(
                        "email" to email,
                        "phone" to phone
                    )
                    user?.uid?.let { userId ->
                        db.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                // Datos del usuario guardados exitosamente en Firestore
                                val intent = Intent(this, HomeFragment::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                // Error al guardar los datos del usuario en Firestore
                                Toast.makeText(this@SignUp, "Error al guardar los datos del usuario: ${e.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Registro fallido
                    Toast.makeText(this@SignUp, "Error en el registro: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}