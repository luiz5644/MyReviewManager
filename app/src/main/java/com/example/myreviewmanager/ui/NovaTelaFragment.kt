package com.example.myreviewmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView // Import adicionado para tipagem
import android.view.KeyEvent // Import adicionado para tipagem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myreviewmanager.data.remote.model.Movie
import com.example.myreviewmanager.databinding.FragmentNovaTelaBinding
import com.example.myreviewmanager.repository.MovieSearchRepository
import com.example.myreviewmanager.ui.adapter.MovieSearchAdapter
import com.example.myreviewmanager.viewmodel.MovieSearchViewModel
import com.example.myreviewmanager.viewmodel.MovieSearchViewModelFactory

class NovaTelaFragment : Fragment() {

    // 1. DECLARAÇÕES DO BINDING E VIEWMODEL
    private var _binding: FragmentNovaTelaBinding? = null
    private val binding get() = _binding!! // Acesso seguro ao binding

    private lateinit var viewModel: MovieSearchViewModel
    private lateinit var adapter: MovieSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicialização do _binding
        _binding = FragmentNovaTelaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        // Inicializa o Repositório e a ViewModelFactory
        val repository = MovieSearchRepository()
        val factory = MovieSearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MovieSearchViewModel::class.java]
    }

    private fun setupRecyclerView() {
        // Inicializa o adaptador, definindo a ação ao clicar em um filme:
        adapter = MovieSearchAdapter { movie ->
            // Passa o filme selecionado para ser revisado na tela principal
            handleMovieSelection(movie)
        }

        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupClickListeners() {
        // Ação ao clicar no botão de busca (binding.btnSearch)
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Ação ao pressionar ENTER no EditText (binding.etSearchQuery)
        // CORREÇÃO APLICADA: Tipagem explícita para resolver o erro "Cannot infer type for this parameter"
        binding.etSearchQuery.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    // Lógica para iniciar a busca
    private fun performSearch() {
        val query = binding.etSearchQuery.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchMovies(query)
        } else {
            Toast.makeText(requireContext(), "Digite um termo de busca.", Toast.LENGTH_SHORT).show()
        }
    }

    // Observação dos dados da ViewModel
    private fun observeViewModel() {
        // Observa a lista de resultados e atualiza o adaptador
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            adapter.updateMovies(movies ?: emptyList())
        }

        // Observa o estado de carregamento (Opcional, para feedback visual)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Exemplo: if (isLoading) { binding.progressBar.visibility = View.VISIBLE } else { binding.progressBar.visibility = View.GONE }
        }

        // Observa mensagens de erro (incluindo o erro de API Key ou rede)
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Lógica para lidar com a seleção de um filme
    private fun handleMovieSelection(movie: Movie) {
        MainActivity.pendingMovieForReview = movie

        // Agora, usamos o contexto do Fragmento para mostrar o Toast:
        Toast.makeText(
            requireContext(),
            "Filme '${movie.title}' selecionado. Vá para 'Minhas Reviews' para criar a avaliação.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência do binding para evitar vazamentos de memória
    }
}