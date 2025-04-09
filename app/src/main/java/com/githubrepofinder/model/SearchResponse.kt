package com.githubrepofinder.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("incomplete_results") val incompleteResults: Boolean,
    @SerializedName("items") val items: List<RepoItem>
)

data class RepoItem(
    val id: Long,
    val name: String,
    @SerializedName("html_url") val htmlUrl: String,
    val owner: Owner,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    val language: String?
)

data class Owner(
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String
)
