package com.example.myreviewmanager.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.data.ReviewDatabase
import com.example.myreviewmanager.databinding.ActivityMainBinding
import com.example.myreviewmanager.databinding.DialogReviewBinding
import com.example.myreviewmanager.repository.ReviewRepository
import com.example.myreviewmanager.ui.adapter.ReviewAdapter
import com.example.myreviewmanager.viewmodel.ReviewViewModel
import com.example.myreviewmanager.viewmodel.ReviewViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var adapter: ReviewAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeReviews()
    }

    private fun setupViewModel() {

        val database = ReviewDatabase.getDatabase(applicationContext)
        val repository = ReviewRepository(database.reviewDao())

        val viewModelFactory = ReviewViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ReviewViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ReviewAdapter(
            onItemClick = { review ->
                showReviewDialog(review)
            },
            onLongItemClick = { review ->
                showDeleteConfirmation(review)
            }
        )
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = adapter
    }

    private fun showDeleteConfirmation(review: Review) {
        AlertDialog.Builder(this)
            .setTitle("Apagar Review")
            .setMessage("Você quer apagar '${review.title}'?")
            .setPositiveButton("Apagar") { _, _ ->
                viewModel.deleteReview(review)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeReviews() {
        viewModel.allReviews.observe(this) { reviews ->
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

    // =================================================================
    // FUNÇÃO SHOWREVIEWDIALOG COM A LÓGICA TOTALMENTE CORRIGIDA
    // =================================================================
    private fun showReviewDialog(existingReview: Review? = null) {

        val dialogBinding = DialogReviewBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        if (existingReview != null) {

            dialogBinding.etTitle.setText(existingReview.title)
            dialogBinding.etDescription.setText(existingReview.description)

            when (existingReview.priority) {
                1L -> dialogBinding.rbLow.isChecked = true
                2L -> dialogBinding.rbMedium.isChecked = true
                3L -> dialogBinding.rbHigh.isChecked = true
            }

            dialogBinding.btnSave.text = "Atualizar"
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString().trim()
            val description = dialogBinding.etDescription.text.toString().trim()

            if (title.isEmpty()) {
                dialogBinding.etTitle.error = "title is required"
                return@setOnClickListener
            }

            val priority = when (dialogBinding.rgPriority.checkedRadioButtonId) {
                R.id.rbLow -> 1
                R.id.rbMedium -> 2
                R.id.rbHigh -> 3
                else -> 1
            }

            if (existingReview != null) {

                val updatedReview = existingReview.copy(
                    title = title,
                    description = description,
                    priority = priority.toLong()
                )
                viewModel.updateReview(updatedReview)
            } else {

                val newReview = Review(
                    title = title,
                    description = description,
                    priority = priority.toLong()
                )
                viewModel.insertReview(newReview)
            }

            dialog.dismiss()
        }
        dialog.show()
    }
}
