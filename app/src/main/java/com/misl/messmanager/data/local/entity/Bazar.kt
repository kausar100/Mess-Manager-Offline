package com.misl.messmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bazar_list")
data class Bazar(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis() // Store date as timestamp
)