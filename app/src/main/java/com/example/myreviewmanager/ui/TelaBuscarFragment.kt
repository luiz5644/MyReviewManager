package com.example.myreviewmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.repository.MovieSearchRepository
import com.example.myreviewmanager.ui.adapter.MovieSearchAdapter
import com.example.myreviewmanager.viewmodel.MovieSearchViewModel
import com.example.myreviewmanager.viewmodel.MovieSearchViewModelFactory

class TelaBuscarFragment : Fragment() {

    private lateinit var viewModel: MovieSearchViewModel
    private lateinit var adapter: MovieSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_nova_tela, container, false)

        // ViewModel
        val factory = MovieSearchViewModelFactory(MovieSearchRepository())
        viewModel = ViewModelProvider(this, factory)[MovieSearchViewModel::class.java]

        // Adapter COM CLIQUE
        adapter = MovieSearchAdapter { movie ->
            onMovieSelected(movie)
        }

        // RecyclerView
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvSearchResults)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Botão buscar
        view.findViewById<ImageButton>(R.id.btnSearch).setOnClickListener {
            val query = view.findViewById<EditText>(R.id.etSearchQuery).text.toString()
            if (query.isNotBlank()) {
                viewModel.searchMovies(query)
            } else {
                Toast.makeText(requireContext(), "Digite o nome do filme", Toast.LENGTH_SHORT).show()
            }
        }

        // Observers
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            adapter.updateMovies(movies)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    /**
     * 🚀 AQUI A MÁGICA ACONTECE
     */
    private fun onMovieSelected(movie: Movie) {
        val mainActivity = requireActivity() as MainActivity

        // 1️⃣ Salva o filme
        MainActivity.pendingMovieForReview = movie

        // 2️⃣ Navega para a aba Reviews
        mainActivity.navigateToReviewsTab()
    }
}
