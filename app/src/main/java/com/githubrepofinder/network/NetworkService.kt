package com.githubrepofinder.network

import com.githubrepofinder.model.SearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class NetworkService {

    private val apiService: GitHubApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cache(createCache())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GitHubApiService::class.java)
    }

    private fun createCache(): Cache? {
        val cacheSize = 10 * 1024 * 1024 // 10MB cache
        return try {
            Cache(File(System.getProperty("java.io.tmpdir"), "github_cache"), cacheSize.toLong())
        } catch (e: Exception) {
            null
        }
    }

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

    suspend fun searchRepositories(query: String): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchRepositories(query)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.Success(body)
                    } else {
                        Result.Error(Exception("Empty response body"))
                    }
                } else {
                    Result.Error(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}