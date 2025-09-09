package com.example.myapplication.orderData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Order::class],
    version = 1,
    exportSchema = false
)
abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var Instance: OrderDatabase? = null

        fun getDatabase(context: Context): OrderDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    OrderDatabase::class.java,
                    "order_database"
                ).build().also { Instance = it }
            }
        }
    }
}