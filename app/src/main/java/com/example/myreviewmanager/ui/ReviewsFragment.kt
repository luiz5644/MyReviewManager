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
import com.example.myreviewmanager.R // Import necessário para acessar R.id
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.data.ReviewDatabase

import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.databinding.FragmentReviewsBinding
import com.example.myreviewmanager.databinding.DialogReviewBinding
import com.example.myreviewmanager.repository.ReviewRepository
import com.example.myreviewmanager.ui.adapter.ReviewAdapter
import com.example.myreviewmanager.data.UserManager
import com.example.myreviewmanager.viewmodel.ReviewViewModel
import com.example.myreviewmanager.viewmodel.ReviewViewModelFactory

class ReviewsFragment : Fragment() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var adapter: ReviewAdapter
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    // ... (onCreateView, onViewCreated, setupViewModel, setupRecyclerView, observeReviews permanecem inalterados) ...

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
        observeReviews()

        // NOVO: Verifica se há um filme pendente vindo da NovaTelaFragment
        if (MainActivity.pendingMovieForReview != null) {
            val movie = MainActivity.pendingMovieForReview!!
            startNewReviewFromMovie(movie)
            MainActivity.pendingMovieForReview = null // Limpa o estado
        }
    }

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


    private fun showReviewDialog(existingReview: Review? = null, movieFromSearch: Movie? = null) {
        val dialogBinding = DialogReviewBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        var tmdbIdToSave: String? = null
        val isEditing = existingReview != null

        val currentUserId = UserManager.requireUserId

        // --- PREENCHIMENTO E DEFINIÇÃO DO ESTADO ---
        if (isEditing) {
            dialogBinding.etTitle.setText(existingReview!!.title)

            // CORREÇÃO 1: CARREGAR A DESCRIÇÃO NO CAMPO DE EDIÇÃO
            dialogBinding.etDescription.setText(existingReview.description)

            // CORREÇÃO 2: CARREGAR A PRIORIDADE/AVALIAÇÃO
            when (existingReview.priority) {
                1L -> dialogBinding.rgPriority.check(R.id.rbLow)
                2L -> dialogBinding.rgPriority.check(R.id.rbMedium)
                3L -> dialogBinding.rgPriority.check(R.id.rbHigh)
            }

            tmdbIdToSave = existingReview.tmdbId
            dialog.setTitle("Editar Review")
        } else if (movieFromSearch != null) {
            dialogBinding.etTitle.setText(movieFromSearch.title)
            dialogBinding.etTitle.isEnabled = false
            tmdbIdToSave = movieFromSearch.imdbID
            dialog.setTitle("Nova Review: ${movieFromSearch.title}")
        } else {
            dialog.setTitle("Adicionar Nova Review")
        }

        // --- LÓGICA DE SALVAMENTO ---
        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()
            val description = dialogBinding.etDescription.text.toString().trim()

            // CORREÇÃO 3: LER A PRIORIDADE/AVALIAÇÃO CORRETA DO RADIO GROUP
            val priority = when (dialogBinding.rgPriority.checkedRadioButtonId) {
                R.id.rbLow -> 1L // "Bom"
                R.id.rbMedium -> 2L // "Médio"
                R.id.rbHigh -> 3L // "Ruim"
                else -> 1L // Padrão se nada estiver selecionado
            }

            if (title.isNotEmpty() && description.isNotEmpty()) {

                val reviewToSave = Review(
                    id = existingReview?.id ?: 0,
                    title = title,
                    userId = existingReview?.userId ?: currentUserId,
                    tmdbId = tmdbIdToSave,
                    description = description,
                    priority = priority, // Usa a prioridade lida do RadioGroup
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