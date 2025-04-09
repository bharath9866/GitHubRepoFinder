package com.githubrepofinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    // Get repositories from the database
    val repositories: LiveData<List<GHRepo>> = repository.getAllRepositories()

    // Search for repositories
    fun searchRepositories(query: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            when (val result = repository.refreshRepositories(query)) {
                is NetworkService.Result.Success -> {
                    _isLoading.value = false
                }
                is NetworkService.Result.Error -> {
                    _error.value = "Error loading repositories: ${result.exception.message}"
                    _isLoading.value = false
                }
            }
        }
    }

    // Search local repositories
    fun searchLocalRepositories(query: String): LiveData<List<GHRepo>> {
        return repository.searchLocalRepositories(query)
    }

    // Clear error message
    fun clearError() {
        _error.value = null
    }

    // Factory for creating the ViewModel with dependencies
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