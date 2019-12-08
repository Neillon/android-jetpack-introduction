package com.neillon.dogs.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {

    @Insert
    suspend fun insert(vararg dogs: Dog): List<Long>

    @Query("SELECT * FROM dog")
    suspend fun getDogs(): List<Dog>

    @Query("SELECT * FROM dog WHERE uuid = :id")
    suspend fun getDogsById(id: Int): Dog

    @Query("DELETE FROM dog")
    suspend fun deleteAll()
}