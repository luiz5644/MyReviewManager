package com.example.myreviewmanager.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myreviewmanager.data.ReviewDatabase
import com.example.myreviewmanager.data.User
import com.example.myreviewmanager.data.UserDao
import com.example.myreviewmanager.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityRegister : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = ReviewDatabase.getDatabase(applicationContext)
        userDao = database.userDao()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val username = binding.etUser.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha usuário e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            val existingUser = userDao.findUserByUsername(username)

            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ActivityRegister,
                        "Usuário '$username' já existe!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@launch
            }

            val newUser = User(username = username, password = password)
            val result = userDao.insertUser(newUser)

            Log.d("REGISTER_TEST", "Usuário cadastrado com id: $result")

            withContext(Dispatchers.Main) {
                Toast.makeText(this@ActivityRegister, "Cadastro realizado!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
