package com.githubrepofinder

import android.app.Application
import com.githubrepofinder.cache.Cacher
import com.githubrepofinder.db.AppDatabase
import com.githubrepofinder.network.NetworkService
import com.githubrepofinder.repository.GitHubRepository

class GitHubRepoFinderApplication : Application() {
    // Lazy dependencies for the app
    val database by lazy { AppDatabase.getDatabase(this) }
    val cacher by lazy { Cacher(database.repoDao()) }
    val networkService by lazy { NetworkService() }
    val repository by lazy { GitHubRepository(networkService, cacher) }
}