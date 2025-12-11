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
        val username = binding.etUser.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha usuário e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            val user = userDao.getUserByCredentials(username, password)
            Log.d("TESTE_LOGIN", "User encontrado: $user")

            withContext(Dispatchers.Main) {
                if (user != null) {

                    // CORRIGIDO: SALVAR O ID DO USUÁRIO LOGADO
                    UserManager.setLoggedInUser(user.id) // Presumindo que sua entidade User tem um campo 'id' do tipo Long

                    Toast.makeText(this@ActivityLogin, "Bem-vindo, $username!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ActivityLogin, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ActivityLogin, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
