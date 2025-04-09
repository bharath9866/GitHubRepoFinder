package com.githubrepofinder.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "repositories")
data class GHRepo(
    @PrimaryKey
    val id: Long,
    val name: String,
    val repoURL: String,
    val ownerLogin: String, // To store the user name
    val description: String?,
    val stars: Int,
    val language: String?
) : Serializable
