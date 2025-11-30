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

        // =============================================================
        // FUNÇÃO BIND CORRIGIDA PARA SINCRONIZAR AS CORES
        // =============================================================
        fun bind(review: Review, onItemClick: (Review) -> Unit, onLongItemClick: (Review) -> Unit) {
            // Define os textos para título e descrição
            binding.tvReviewTitle.text = review.title
            binding.tvReviewDescription.text = review.description

            // Pega a cor e o texto da data class para reutilizar
            val priorityColor = review.getPriorityColor().toInt() // Converte para Int aqui
            val priorityText = review.getPriorityText()

            // Define o texto do "botão" de prioridade
            binding.tvPriority.text = priorityText

            // Define a cor da linha indicadora
            binding.viewPriorityIndicator.setBackgroundColor(priorityColor)

            // Define a cor do FUNDO do "botão" de prioridade para ser a MESMA da linha
            binding.tvPriority.background.setTint(priorityColor)

            // Define a cor do título (lógica de "completado" foi removida)
            val context = binding.root.context
            binding.tvReviewTitle.setTextColor(ContextCompat.getColor(context, R.color.black))

            // Define os listeners de clique
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
