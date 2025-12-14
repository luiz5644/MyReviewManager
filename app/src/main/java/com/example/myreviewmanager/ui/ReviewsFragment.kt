package com.example.myreviewmanager.ui

import android.content.Intent
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
import com.example.myreviewmanager.data.UserManager
import com.example.myreviewmanager.viewmodel.ReviewViewModel
import com.example.myreviewmanager.viewmodel.ReviewViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // ==============================================================
        // IMPLEMENTAÇÃO DO LOGOUT NO BOTÃO
        // ==============================================================
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
        // ==============================================================

        setupViewModel()
        setupRecyclerView()
        observeReviews()

        // Verifica se há um filme pendente vindo da NovaTelaFragment
        if (MainActivity.pendingMovieForReview != null) {
            val movie = MainActivity.pendingMovieForReview!!
            startNewReviewFromMovie(movie)
            MainActivity.pendingMovieForReview = null // Limpa o estado
        }
    }

    private fun performLogout() {
        // 1. Limpa os dados do usuário na sessão
        UserManager.logout()

        // 2. Navega para a Activity de Login, limpando a pilha de atividades
        val intent = Intent(requireContext(), ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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

            // Carrega Descrição e Avaliação
            dialogBinding.etDescription.setText(existingReview.description)
            when (existingReview.priority) {
                1L -> dialogBinding.rgPriority.check(R.id.rbLow)
                2L -> dialogBinding.rgPriority.check(R.id.rbMedium)
                3L -> dialogBinding.rgPriority.check(R.id.rbHigh)
            }

            tmdbIdToSave = existingReview.tmdbId
            dialog.setTitle("Editar Review")

            // MUDAR O TEXTO DO BOTÃO PARA ATUALIZAR
            dialogBinding.btnSave.text = "Atualizar"

        } else if (movieFromSearch != null) {
            dialogBinding.etTitle.setText(movieFromSearch.title)
            dialogBinding.etTitle.isEnabled = false
            tmdbIdToSave = movieFromSearch.imdbID
            dialog.setTitle("Nova Review: ${movieFromSearch.title}")
        } else {
            dialog.setTitle("Adicionar Nova Review")
        }

        // IMPLEMENTAR LISTENER PARA O BOTÃO CANCELAR
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss() // Apenas fecha o diálogo
        }

        // --- LÓGICA DE SALVAMENTO COM VALIDAÇÃO DE DUPLICIDADE (CORRIGIDA) ---
        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()

            // =================================================================
            // CORREÇÃO ESSENCIAL: Adicionando .text para pegar o conteúdo digitado
            val description = dialogBinding.etDescription.text.toString().trim()
            // =================================================================

            val priority = when (dialogBinding.rgPriority.checkedRadioButtonId) {
                R.id.rbLow -> 1L // "Bom"
                R.id.rbMedium -> 2L // "Médio"
                R.id.rbHigh -> 3L // "Ruim"
                else -> 1L // Padrão
            }

            if (title.isNotEmpty() && description.isNotEmpty()) {

                // Usamos CoroutineScope(Dispatchers.IO) para chamar a função suspensa
                CoroutineScope(Dispatchers.IO).launch {

                    if (!isEditing && tmdbIdToSave != null) {
                        // 1. Checa duplicidade SOMENTE se for uma NOVA review (não edição)
                        val existingReviewForMovie = viewModel.checkExistingReview(tmdbIdToSave, currentUserId)

                        if (existingReviewForMovie != null) {
                            // Se a review já existe, notifica o usuário na thread principal e sai
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Você já adicionou uma review para este filme.", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                            return@launch // Sai da coroutine
                        }
                    }

                    // Se for edição OU se for nova review e não houver duplicidade, salva.
                    val reviewToSave = Review(
                        id = existingReview?.id ?: 0,
                        title = title,
                        userId = existingReview?.userId ?: currentUserId,
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

                    // Fecha o diálogo na thread principal
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                    }
                }
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