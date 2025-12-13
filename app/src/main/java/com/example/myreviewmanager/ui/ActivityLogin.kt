package com.example.myreviewmanager.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myreviewmanager.data.ReviewDatabase
import com.example.myreviewmanager.data.UserDao
import com.example.myreviewmanager.data.UserManager
import com.example.myreviewmanager.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityLogin : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // =========================================================
        // CORREÇÃO ESSENCIAL: VERIFICAR USUÁRIO LOGADO AO INICIAR
        // Se já houver um ID de usuário na sessão, redireciona
        // para a MainActivity imediatamente.
        // =========================================================
        if (UserManager.currentUserId != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Fecha a ActivityLogin
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = ReviewDatabase.getDatabase(applicationContext)
        userDao = database.userDao()

        setupListeners()
    }

    private fun setupListeners() {

        binding.btnEnter.setOnClickListener {
            performLogin()
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, ActivityRegister::class.java))
        }
    }

    private fun performLogin() {
        val email = binding.etUser.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            // Busca o usuário no banco de dados
            val user = userDao.getUserByCredentials(email, password)
            Log.d("TESTE_LOGIN", "User encontrado: $user")

            withContext(Dispatchers.Main) {
                if (user != null) {

                    // Salva o ID e o nome/e-mail no UserManager
                    UserManager.setLoggedInUser(user.id, user.username)

                    Toast.makeText(this@ActivityLogin, "Bem-vindo(a), $email!", Toast.LENGTH_SHORT).show()

                    // Redireciona para a tela principal
                    startActivity(Intent(this@ActivityLogin, MainActivity::class.java))
                    finish() // Fecha a ActivityLogin para que o botão "Voltar" não retorne a ela
                } else {
                    Toast.makeText(this@ActivityLogin, "E-mail ou senha inválidos.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}