package com.githubrepofinder.repository

import androidx.lifecycle.LiveData
import com.githubrepofinder.cache.Cacher
import com.githubrepofinder.model.GHRepo
import com.githubrepofinder.model.RepoItem
import com.githubrepofinder.network.NetworkService

class GitHubRepository(
    private val networkService: NetworkService,
    private val cacher: Cacher
) {
    fun getAllRepositories(): LiveData<List<GHRepo>> {
        return cacher.getAllRepositories()
    }

    fun searchLocalRepositories(query: String): LiveData<List<GHRepo>> {
        return cacher.searchRepositories(query)
    }

    suspend fun refreshRepositories(query: String): NetworkService.Result<Boolean> {
        return when (val response = networkService.searchRepositories(query)) {
            is NetworkService.Result.Success -> {
                cacher.cacheRepositories(response.data.items)
                NetworkService.Result.Success(true)
            }
            is NetworkService.Result.Error -> {
                NetworkService.Result.Error(response.exception)
            }
        }
    }
}