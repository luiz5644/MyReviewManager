package com.example.myreviewmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myreviewmanager.R // Importe R se necessário, dependendo da sua versão

import com.example.myreviewmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Encontrar o NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Conectar a BottomNavigationView ao NavController
        binding.bottomNavigation.setupWithNavController(navController)

        // Nota: A lógica da sua tela de reviews foi movida para o ReviewsFragment.kt
    }
}