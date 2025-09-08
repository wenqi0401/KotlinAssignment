package com.example.myapplication.registerData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class],
    version = 4,  // Increment to version 4 for new schema
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "mixue_user_database_v4"  // New database name for version 4
                )
                    .fallbackToDestructiveMigration() // Recreates database if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}