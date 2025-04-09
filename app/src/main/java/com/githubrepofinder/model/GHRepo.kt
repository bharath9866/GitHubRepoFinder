package com.githubrepofinder.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "repositories",
    indices = [
        Index(value = ["name", "ownerLogin"], unique = true),
        Index(value = ["language"])
    ]
)
data class GHRepo(
    @PrimaryKey val id: Long,
    val name: String,
    val repoURL: String,
    val ownerLogin: String,
    val description: String?,
    val stars: Int,
    val language: String?
) : Serializable