package com.misl.messmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "meals",
    foreignKeys = [ForeignKey(
        entity = Member::class,
        parentColumns = ["id"],
        childColumns = ["memberId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val memberId: Int,
    val date: String, // YYYY-MM-DD format for easy querying
    val breakfast: Double = 0.0, // 0.5
    val lunch: Double = 0.0,     // 1.0
    val dinner: Double = 0.0     // 1.0
)