package com.example.myreviewmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Variável estática para carregar o filme da tela de busca para a tela de reviews
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

    // Método que você pode usar para salvar o filme, se necessário
    fun initiateReviewForMovie(movie: Movie) {
        pendingMovieForReview = movie
    }

    // =================================================================
    // FUNÇÃO CRUCIAL: ALTERA A ABA PARA A TELA DE REVIEWS NO CLIQUE DO FILME
    // =================================================================
    /**
     * Altera o item selecionado na BottomNavigationView, levando o usuário
     * diretamente para o Fragmento de Reviews.
     */
    fun navigateToReviewsTab() {
        // IMPORTANTE: O ID 'R.id.reviewsFragment' deve corresponder ao ID do
        // item do menu da sua aba de Reviews no arquivo 'bottom_nav_menu.xml'.
        try {
            binding.bottomNavigation.selectedItemId = R.id.reviewsFragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}