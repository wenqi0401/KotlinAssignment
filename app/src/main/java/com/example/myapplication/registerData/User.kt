package com.example.myapplication.registerData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class loginUiState (
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    var username: String="",
    var password: String =""
    //confirmPassword: String =""
)