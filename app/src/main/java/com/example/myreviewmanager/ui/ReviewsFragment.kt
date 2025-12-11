package com.example.myreviewmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.data.ReviewDatabase
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.databinding.FragmentReviewsBinding
import com.example.myreviewmanager.databinding.DialogReviewBinding
import com.example.myreviewmanager.repository.ReviewRepository
import com.example.myreviewmanager.ui.adapter.ReviewAdapter
import com.example.myreviewmanager.viewmodel.ReviewViewModel
import com.example.myreviewmanager.viewmodel.ReviewViewModelFactory

class ReviewsFragment : Fragment() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var adapter: ReviewAdapter
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeReviews()

        // NOVO: Verifica se há um filme pendente vindo da NovaTelaFragment
        if (MainActivity.pendingMovieForReview != null) {
            val movie = MainActivity.pendingMovieForReview!!
            startNewReviewFromMovie(movie)
            MainActivity.pendingMovieForReview = null // Limpa o estado
        }
    }

    // NOVO: Função auxiliar para iniciar a review a partir de um Movie
    private fun startNewReviewFromMovie(movie: Movie) {
        showReviewDialog(
            existingReview = null,
            movieFromSearch = movie
        )
    }

    private fun setupViewModel() {
        val database = ReviewDatabase.getDatabase(requireContext())
        val repository = ReviewRepository(database.reviewDao())
        val viewModelFactory = ReviewViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ReviewViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ReviewAdapter(
            onItemClick = { review -> showReviewDialog(existingReview = review) },
            onLongItemClick = { review -> showDeleteConfirmation(review) }
        )
        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = adapter
    }

    private fun observeReviews() {
        viewModel.allReviews.observe(viewLifecycleOwner) { reviews ->
            adapter.updateReviews(reviews)
            if (reviews.isEmpty()) {
                binding.rvReviews.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.rvReviews.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        // O clique no botão 'Adicionar nova review' agora abre o diálogo sem preenchimento
        binding.btnAddReview.setOnClickListener {
            showReviewDialog()
        }
    }

    private fun showDeleteConfirmation(review: Review) {
        AlertDialog.Builder(requireContext())
            .setTitle("Apagar Review")
            .setMessage("Você quer apagar '${review.title}'?")
            .setPositiveButton("Apagar") { _, _ ->
                viewModel.deleteReview(review)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // FUNÇÃO MODIFICADA: Agora aceita um Movie opcional
    private fun showReviewDialog(existingReview: Review? = null, movieFromSearch: Movie? = null) {
        val dialogBinding = DialogReviewBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // CORRIGIDO: Mudei para String? para receber o imdbID
        var tmdbIdToSave: String? = null
        val isEditing = existingReview != null

        // --- PREENCHIMENTO E DEFINIÇÃO DO ESTADO ---
        if (isEditing) {
            dialogBinding.etTitle.setText(existingReview!!.title)
            // Assumindo que você tenha um campo para 'description'
            // dialogBinding.etDescription.setText(existingReview.description)
            // Usa o tmdbId, que agora deve ser String? na classe Review
            tmdbIdToSave = existingReview.tmdbId
            dialog.setTitle("Editar Review")
        } else if (movieFromSearch != null) {
            // Se veio da busca
            dialogBinding.etTitle.setText(movieFromSearch.title)
            dialogBinding.etTitle.isEnabled = false // Título do filme é fixo
            // CORRIGIDO: Usa o imdbID padronizado da classe Movie (OMDb)
            tmdbIdToSave = movieFromSearch.imdbID
            dialog.setTitle("Nova Review: ${movieFromSearch.title}")
        } else {
            dialog.setTitle("Adicionar Nova Review")
        }

        // --- LÓGICA DE SALVAMENTO ---
        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()
            val description = dialogBinding.etDescription.text.toString().trim()

            // TODO: Substitua 1L pela lógica real de obter a prioridade/rating do seu DialogReviewBinding
            val priority = 1L

            if (title.isNotEmpty() && description.isNotEmpty()) {

                val reviewToSave = Review(
                    id = existingReview?.id ?: 0,
                    title = title,
                    // CORRIGIDO: Usa tmdbIdToSave (String?)
                    tmdbId = tmdbIdToSave,
                    description = description,
                    priority = priority,
                    createdAt = existingReview?.createdAt ?: System.currentTimeMillis()
                )

                if (isEditing) {
                    viewModel.updateReview(reviewToSave)
                } else {
                    viewModel.insertReview(reviewToSave)
                }

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Preencha Título e Descrição", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}