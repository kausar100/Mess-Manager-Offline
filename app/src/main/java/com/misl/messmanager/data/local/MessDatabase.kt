package com.misl.messmanager.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.misl.messmanager.data.local.dao.MessDao
import com.misl.messmanager.data.local.entity.Bazar
import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Meal
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.data.local.entity.UtilityBill


@Database(entities = [Member::class, Bazar::class, Meal::class, Deposit::class, UtilityBill::class], version = 1)
abstract class MessDatabase : RoomDatabase() {
    abstract fun messDao(): MessDao
}