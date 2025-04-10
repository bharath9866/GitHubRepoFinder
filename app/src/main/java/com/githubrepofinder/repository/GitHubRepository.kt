package com.githubrepofinder.repository

import androidx.lifecycle.LiveData
import com.githubrepofinder.cache.Cacher
import com.githubrepofinder.model.GHRepo
import com.githubrepofinder.network.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that manages GitHub repository data operations.
 * It serves as a single source of truth for accessing GitHub repositories data
 * by fetching from network and caching data locally.
 *
 * @property networkService Service responsible for network operations
 * @property cacher Service responsible for local caching operations
 */
class GitHubRepository(
    private val networkService: NetworkService,
    private val cacher: Cacher
) {
    /**
     * Gets all repositories from local cache.
     *
     * @return LiveData object containing a list of GitHub repositories
     */
    fun getAllRepositories(): LiveData<List<GHRepo>> {
        return cacher.getAllRepositories()
    }

    /**
     * Searches repositories in local cache based on a query string.
     *
     * @param query The search term to filter repositories
     * @return LiveData object containing a filtered list of GitHub repositories
     */
    fun searchLocalRepositories(query: String): LiveData<List<GHRepo>> {
        return cacher.searchRepositories(query)
    }

    /**
     * Gets repositories with appropriate filtering based on query.
     * Simplifies viewModel by handling query logic in repository layer.
     *
     * @param query The search term to filter repositories (empty string returns all)
     * @return LiveData object containing filtered or all GitHub repositories
     */
    fun getRepositories(query: String?): LiveData<List<GHRepo>> {
        return if (query.isNullOrEmpty()) {
            getAllRepositories()
        } else {
            searchLocalRepositories(query)
        }
    }

    /**
     * Refreshes repositories by fetching new data from the network and updating cache.
     * 
     * @param query The search term to fetch repositories from network
     * @return Result object indicating success or failure of the operation
     */
    suspend fun refreshRepositories(query: String): NetworkService.Result<Boolean> {
        return withContext(Dispatchers.IO) {
            when (val response = networkService.searchRepositories(query)) {
                is NetworkService.Result.Success -> {
                    // Cache the retrieved repositories
                    cacher.cacheRepositories(response.data.items)
                    NetworkService.Result.Success(true)
                }

                is NetworkService.Result.Error -> {
                    // Forward the error from network service
                    NetworkService.Result.Error(response.exception)
                }
            }
        }
    }
}