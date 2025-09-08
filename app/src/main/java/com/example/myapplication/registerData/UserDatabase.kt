package com.example.myapplication.registerData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.orderData.Order
import com.example.myapplication.orderData.OrderConverters
import com.example.myapplication.orderData.OrderDao


@Database(
    entities = [User::class, Order::class],
    version = 5,

    exportSchema = false
)

@TypeConverters(OrderConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "mixue_user_database_v5"  // Change database name to force new creation
                )
                    .fallbackToDestructiveMigration() // This recreates database if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
