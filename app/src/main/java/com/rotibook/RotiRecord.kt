package com.rotibook

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phoneNumber: String
)

@Entity(tableName = "roti_purchases")
data class RotiPurchase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val quantity: Int,
    val date: Date
)




