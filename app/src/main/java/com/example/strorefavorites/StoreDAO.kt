package com.example.strorefavorites

import androidx.room.*

@Dao
interface StoreDAO {
    //declaracion de todas las consultas a utilizar
    @Query("SELECT * FROM StoreEntity")
    fun getAllStore() : MutableList<StoreEntity>
    @Query("SELECT * FROM StoreEntity WHERE id=:id")
    fun getStoreByI1(id:Long):StoreEntity
    @Insert
    fun addStore(storeEntity: StoreEntity) : Long
    @Update
    fun upDateStore(storeEntity: StoreEntity)
    @Delete
    fun deleteStore(storeEntity: StoreEntity)
}