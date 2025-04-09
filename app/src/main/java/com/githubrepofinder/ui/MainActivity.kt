package com.githubrepofinder.ui

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.githubrepofinder.cache.Cacher
import com.githubrepofinder.databinding.ActivityMainBinding
import com.githubrepofinder.db.AppDatabase
import com.githubrepofinder.network.NetworkService
import com.githubrepofinder.repository.GitHubRepository
import com.githubrepofinder.ui.WebViewActivity
import com.githubrepofinder.ui.adapter.RepositoryAdapter
import com.githubrepofinder.viewmodel.RepositoryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RepositoryViewModel
    private lateinit var adapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dependencies
        val database = AppDatabase.getDatabase(applicationContext)
        val repoDao = database.repoDao()
        val networkService = NetworkService()
        val cacher = Cacher(repoDao)
        val repository = GitHubRepository(networkService, cacher)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this, RepositoryViewModel.Factory(repository))[RepositoryViewModel::class.java]

        // Setup RecyclerView and Adapter
        setupRecyclerView()
        
        // Observe data changes
        observeViewModel()

        // Initial search for repositories
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchRepositories(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        observeLocalSearch(it)
                    } else {
                        observeViewModel()
                    }
                }
                return true
            }
        })

        // Initial data load
        viewModel.searchRepositories("language:swift")
    }

    private fun setupRecyclerView() {
        adapter = RepositoryAdapter { repo ->
            // Navigate to WebView
            val intent = WebViewActivity.createIntent(this, repo.repoURL, repo.name)
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.repositories.observe(this) { repos ->
            adapter.submitList(repos)
            binding.emptyView.visibility = if (repos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun observeLocalSearch(query: String) {
        viewModel.searchLocalRepositories(query).observe(this) { repos ->
            adapter.submitList(repos)
            binding.emptyView.visibility = if (repos.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}