package com.githubrepofinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.githubrepofinder.model.GHRepo
import com.githubrepofinder.network.NetworkService
import com.githubrepofinder.repository.GitHubRepository
import kotlinx.coroutines.launch

class RepositoryViewModel(private val repository: GitHubRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Query LiveData to trigger repository updates
    private val _currentQuery = MutableLiveData<String>("")

    // Transform the query into repository results automatically
    val repositories: LiveData<List<GHRepo>> = _currentQuery.switchMap { query ->
        repository.getRepositories(query)
    }

    init {
        // Initial empty query
        _currentQuery.value = ""
    }

    fun refreshRepositories(query: String) {
        _currentQuery.value = query
    }

    /**
     * Fetches repositories from the GitHub API based on the provided search query
     * and updates the local database.
     *
     * @param query The search term to look for in GitHub repositories
     */
    fun searchRepositories(query: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                when (val result = repository.refreshRepositories(query)) {
                    is NetworkService.Result.Success -> {
                        // Update query to show only matching repositories
                        _currentQuery.value = query
                        _isLoading.value = false
                    }

                    is NetworkService.Result.Error -> {
                        _error.value = "Error loading repositories: ${result.exception.message}"
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Factory class for creating RepositoryViewModel instances with the required dependencies
     *
     * @property repository The repository implementation to inject into the ViewModel
     */
    class Factory(private val repository: GitHubRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RepositoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RepositoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}