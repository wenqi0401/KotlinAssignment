// app/src/main/java/com/example/myapplication/di/ServiceLocator.kt
package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.registerData.AppDatabase
import com.example.myapplication.registerData.UserDao
import com.example.myapplication.registerData.UserRepository
import com.example.myapplication.registerData.UserSyncRepository

object ServiceLocator {
    private lateinit var db: AppDatabase
    lateinit var userDao: UserDao
        private set
    lateinit var firebaseRepo: UserRepository
        private set
    lateinit var userSyncRepository: UserSyncRepository
        private set

    fun init(context: Context) {
        if (::db.isInitialized) return
        db = Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()
        userDao = db.userDao()
        firebaseRepo = UserRepository()
        userSyncRepository = UserSyncRepository(firebaseRepo, userDao)
    }
}
