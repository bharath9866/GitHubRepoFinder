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

        initializeDependencies()
        setupRecyclerView()
        setupSearchView()
        setupObservers()

        // Load initial data with Kotlin repositories only if it's a fresh start
        if (savedInstanceState == null) {
            viewModel.searchRepositories("kotlin")
        }
    }

    /**
     * Initializes dependencies needed for the application.
     * Sets up the database, network service, cacher, repository, and ViewModel.
     */
    private fun initializeDependencies() {
        val database = AppDatabase.getDatabase(applicationContext)
        val repoDao = database.repoDao()
        val networkService = NetworkService()
        val cacher = Cacher(repoDao)
        val repository = GitHubRepository(networkService, cacher)

        viewModel = ViewModelProvider(this, RepositoryViewModel.Factory(repository))[RepositoryViewModel::class.java]
    }


    private fun setupSearchView() {
        /**
         * Sets up the SearchView with query listeners.
         * 
         * The SearchView handles two types of user interactions:
         * 1. Query Submission: When user submits a search query by pressing the search button
         *    - If the query is non-empty, it triggers a network API call to search GitHub repositories
         * 
         * 2. Query Text Change: When user types or modifies text in the search field
         *    - Instantly filters the currently loaded repositories based on the search text
         *    - Shows all repositories when search text is empty
         *    - This operation is performed locally and doesn't make network requests
         *    - Provides real-time feedback as user types
         */
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchRepositories(it)
                    }
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.refreshRepositories(it)
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = RepositoryAdapter { repo ->
            // Launch WebView when a repository is clicked
            val intent = WebViewActivity.createIntent(this, repo.repoURL, repo.name)
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true) // Optimization when we know item size doesn't change
        }
    }

    /**
     * Sets up all observers for LiveData objects from ViewModel.
     * A single repository observer is created that stays active throughout the app lifecycle.
     */
    private fun setupObservers() {
        // Set up a single observer for repositories that stays active throughout the app lifecycle
        viewModel.repositories.observe(this) { repos ->
            adapter.submitList(repos)
            binding.emptyView.visibility = if (repos?.isEmpty() == true) View.VISIBLE else View.GONE
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

}