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
                item.toGHRepo()
            }
            repoDao.insertAll(ghRepos)
        }
    }

    private fun RepoItem.toGHRepo(): GHRepo {
        return GHRepo(
            id = this.id,
            name = this.name,
            repoURL = this.htmlUrl,
            ownerLogin = this.owner.login,
            description = this.description,
            stars = this.stars,
            language = this.language
        )
    }

    suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            repoDao.deleteAll()
        }
    }
}