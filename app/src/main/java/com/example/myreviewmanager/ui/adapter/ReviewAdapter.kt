package com.example.myreviewmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myreviewmanager.R
import com.example.myreviewmanager.data.Review
import com.example.myreviewmanager.databinding.ItemReviewBinding

class ReviewAdapter(
    private var reviews: List<Review> = emptyList(),
    private val onItemClick: (Review) -> Unit,
    private val onLongItemClick : (Review) -> Unit,

    ): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(review: Review, onItemClick: (Review) -> Unit, onLongItemClick: (Review) -> Unit) {

            binding.tvReviewTitle.text = review.title
            binding.tvReviewDescription.text = review.description


            val priorityColor = review.getPriorityColor().toInt()
            val priorityText = review.getPriorityText()


            binding.tvPriority.text = priorityText


            binding.viewPriorityIndicator.setBackgroundColor(priorityColor)


            binding.tvPriority.background.setTint(priorityColor)


            val context = binding.root.context
            binding.tvReviewTitle.setTextColor(ContextCompat.getColor(context, R.color.black))


            binding.root.setOnLongClickListener {
                onLongItemClick(review)
                true
            }
            binding.root.setOnClickListener {
                onItemClick(review)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return  ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review, onItemClick, onLongItemClick)
    }

    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
