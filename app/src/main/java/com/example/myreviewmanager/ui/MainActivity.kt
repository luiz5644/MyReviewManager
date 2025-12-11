package com.example.myreviewmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.remote.model.Movie // NOVO IMPORT
import com.example.myreviewmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // NOVO: Variável estática para carregar o filme da tela de busca para a tela de reviews
    companion object {
        var pendingMovieForReview: Movie? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

    // NOVO: Método para iniciar o processo de Review a partir de um filme buscado
    fun initiateReviewForMovie(movie: Movie) {
        pendingMovieForReview = movie
    }
}