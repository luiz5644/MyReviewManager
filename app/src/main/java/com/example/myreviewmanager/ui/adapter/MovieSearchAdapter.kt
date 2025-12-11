package com.example.myreviewmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.databinding.ItemMovieSearchBinding
import com.bumptech.glide.Glide
import com.example.myreviewmanager.R

/**
 * Adaptador para exibir os resultados da busca de filmes.
 * @param onMovieClick Lambda que será chamada quando um filme for clicado.
 */
class MovieSearchAdapter(
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = emptyList()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.bind(movie, onMovieClick)
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    class MovieViewHolder(private val binding: ItemMovieSearchBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(movie: Movie, onMovieClick: (Movie) -> Unit) {
            // Define o título e a sinopse
            binding.tvTitle.text = movie.title

            // ATENÇÃO: O campo 'overview' estará vazio após uma busca 's' do OMDb
            binding.tvOverview.text = movie.overview

            // Define o clique no item
            binding.movieItemContainer.setOnClickListener {
                onMovieClick(movie)
            }


            val posterUrl = movie.posterPath


            if (!posterUrl.isNullOrEmpty() && posterUrl != "N/A") {
                Glide.with(binding.ivPoster.context)
                    // CORRIGIDO: Usa a URL completa fornecida pelo OMDb
                    .load(posterUrl)
                    // Use um recurso de placeholder que você criou (ou o padrão do Android)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel) // Em caso de erro
                    .into(binding.ivPoster)
            } else {
                // Se não houver pôster, define o placeholder ou limpa a imagem
                binding.ivPoster.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }
}