package com.githubrepofinder.cache

import androidx.lifecycle.LiveData
import com.githubrepofinder.db.RepoDao
import com.githubrepofinder.model.GHRepo
import com.githubrepofinder.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Cacher(private val repoDao: RepoDao) {
    fun getAllRepositories(): LiveData<List<GHRepo>> {
        return repoDao.getAllRepos()
    }

    fun searchRepositories(query: String): LiveData<List<GHRepo>> {
        val searchQuery = "%$query%"
        return repoDao.searchRepos(searchQuery)
    }

    suspend fun cacheRepositories(repoItems: List<RepoItem>) {
        withContext(Dispatchers.IO) {
            val ghRepos = repoItems.map { item ->
                GHRepo(
                    id = item.id,
                    name = item.name,
                    repoURL = item.htmlUrl,
                    ownerLogin = item.owner.login,
                    description = item.description,
                    stars = item.stars,
                    language = item.language
                )
            }
            repoDao.insertAll(ghRepos)
        }
    }

    suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            repoDao.deleteAll()
        }
    }
}