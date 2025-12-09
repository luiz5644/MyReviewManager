package com.example.myreviewmanager.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myreviewmanager.databinding.ActivityLoginBinding // Gerado do activity_login.xml

class ActivityLogin : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Credenciais fixas de exemplo
    private val VALID_USER = "luiz"
    private val VALID_PASSWORD = "123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnEnter.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val user = binding.etUser.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha usuário e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        if (user == VALID_USER && password == VALID_PASSWORD) {
            // Login bem-sucedido: Navega para a tela principal (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a tela de login para que o usuário não possa voltar
        } else {
            // Login falhou
            Toast.makeText(this, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show()
        }
    }
}