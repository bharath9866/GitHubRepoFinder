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

    @Query("SELECT * FROM repositories")
    fun getAllRepos(): LiveData<List<GHRepo>>

    @Query("SELECT * FROM repositories WHERE name LIKE :searchQuery OR id LIKE :searchQuery")
    fun searchRepos(searchQuery: String): LiveData<List<GHRepo>>

    @Query("DELETE FROM repositories")
    suspend fun deleteAll()
}