package com.githubrepofinder.network

import com.githubrepofinder.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
//        @Query("per_page") perPage: Int = 30,
//        @Query("page") page: Int = 1
    ): Response<SearchResponse>
}