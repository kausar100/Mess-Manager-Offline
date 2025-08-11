package com.misl.messmanager.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utility_bills")
data class UtilityBill(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monthYear: String, // e.g., "2025-08"
    val name: String,      // e.g., "Gas Bill", "WiFi"
    val amount: Double
)