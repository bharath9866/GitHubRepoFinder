package com.githubrepofinder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.githubrepofinder.model.GHRepo

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<GHRepo>)

    @Query("SELECT * FROM repositories ORDER BY stars DESC")
    fun getAllRepos(): LiveData<List<GHRepo>>

    @Query("SELECT * FROM repositories WHERE name LIKE :searchQuery OR language LIKE :searchQuery OR ownerLogin LIKE :searchQuery OR description LIKE :searchQuery OR id LIKE :searchQuery ORDER BY stars DESC")
    fun searchRepos(searchQuery: String): LiveData<List<GHRepo>>

    @Query("DELETE FROM repositories")
    suspend fun deleteAll()
}