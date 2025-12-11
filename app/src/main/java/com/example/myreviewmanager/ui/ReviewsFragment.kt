package com.example.myreviewmanager.ui

// ... (Imports omitidos para brevidade, mas devem incluir todos os seus imports originais)
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.data.ReviewDatabase
import com.example.myreviewmanager.databinding.FragmentReviewsBinding
import com.example.myreviewmanager.databinding.DialogReviewBinding
import com.example.myreviewmanager.repository.ReviewRepository
import com.example.myreviewmanager.ui.adapter.ReviewAdapter
import com.example.myreviewmanager.viewmodel.ReviewViewModel
import com.example.myreviewmanager.viewmodel.ReviewViewModelFactory
// ...

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
    }

    private fun setupViewModel() {
        val database = ReviewDatabase.getDatabase(requireContext())
        val repository = ReviewRepository(database.reviewDao())
        val viewModelFactory = ReviewViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ReviewViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ReviewAdapter(
            onItemClick = { review -> showReviewDialog(review) },
            onLongItemClick = { review -> showDeleteConfirmation(review) }
        )
        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = adapter
    }

    private fun observeReviews() {
        viewModel.allReviews.observe(viewLifecycleOwner) { reviews -> // Use viewLifecycleOwner
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
        binding.btnAddReview.setOnClickListener {
            showReviewDialog()
        }
    }

    private fun showDeleteConfirmation(review: Review) {
        AlertDialog.Builder(requireContext()) // Use requireContext()
            .setTitle("Apagar Review")
            .setMessage("Você quer apagar '${review.title}'?")
            .setPositiveButton("Apagar") { _, _ ->
                viewModel.deleteReview(review)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showReviewDialog(existingReview: Review? = null) {
        val dialogBinding = DialogReviewBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()) // Use requireContext()
            .setView(dialogBinding.root)
            .create()

        // Lógica de preenchimento e save... (Copie o resto da sua lógica original aqui)
        // ...

        // Exemplo de como usar o dialogBinding.btnSave
        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()
            val description = dialogBinding.etDescription.text.toString().trim()

            // ... (restante da lógica de validação e salvamento/atualização)

            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}