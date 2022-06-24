package com.example.strorefavorites

interface MainAux {
    fun hidFab(isVisible : Boolean = false)
    fun addStore(storeEntity: StoreEntity)
    fun upDateStore(storeEntity: StoreEntity)
}