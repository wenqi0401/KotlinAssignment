package com.example.myapplication.orderData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.voucher.VoucherDao
import com.example.myapplication.voucher.VoucherEntity
import com.example.myapplication.voucher.UserVoucherEntity

@Database(
    entities = [Order::class, VoucherEntity::class, UserVoucherEntity::class],
    version = 3, // Update version number
    exportSchema = false
)

abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun voucherDao(): VoucherDao

    companion object {
        @Volatile
        private var INSTANCE: OrderDatabase? = null

        fun getDatabase(context: Context): OrderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderDatabase::class.java,
                    "order_database"
                )
                    .fallbackToDestructiveMigration() // Handle version change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}