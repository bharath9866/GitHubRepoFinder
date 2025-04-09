package com.githubrepofinder.network

import com.githubrepofinder.model.SearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class NetworkService {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val githubApiService = retrofit.create(GitHubApiService::class.java)

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

    suspend fun searchRepositories(query: String): Result<SearchResponse> {
        return try {
            val response = githubApiService.searchRepositories(query)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(IOException("Error searching repositories: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}