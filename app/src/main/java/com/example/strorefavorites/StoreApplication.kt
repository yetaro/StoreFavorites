package com.example.strorefavorites
import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class StoreApplication() : Application() {
companion object{
    lateinit var dataBase: StoreDataBase
}

    override fun onCreate() {
        super.onCreate()
        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoURL TEXT NOT NULL DEFAULT ''")
            }
        }
        dataBase = Room.databaseBuilder(
            this, StoreDataBase::class.java,
            "StoreDataBases")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}