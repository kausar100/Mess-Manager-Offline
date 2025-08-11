package com.misl.messmanager.data.local.dao

import androidx.room.*
import com.misl.messmanager.data.local.entity.Bazar
import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Meal
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.data.local.entity.UtilityBill
import kotlinx.coroutines.flow.Flow

@Dao
interface MessDao {
    // 1. Modify insertMember to return the ID of the new row
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Delete
    suspend fun deleteBazar(bazar: Bazar)


    // 2. Create a new @Transaction function
    @Transaction
    suspend fun addMemberWithInitialDeposit(member: Member, deposit: Deposit) {
        // Insert the member and get their new auto-generated ID
        val memberId = insertMember(member)
        // Use the new ID to create the corresponding deposit record
        insertDeposit(deposit.copy(memberId = memberId.toInt()))
    }

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    // Bazar Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBazar(bazar: Bazar)

    @Query("SELECT * FROM bazar_list WHERE strftime('%Y-%m', timestamp / 1000, 'unixepoch') = :monthYear")
    fun getBazarForMonth(monthYear: String): Flow<List<Bazar>> // e.g., "2025-08"

    @Query("SELECT SUM(amount) FROM bazar_list WHERE strftime('%Y-%m', timestamp / 1000, 'unixepoch') = :monthYear")
    fun getTotalBazarForMonth(monthYear: String): Flow<Double?>

    // Meal Operations
    @Upsert // Inserts or updates a meal record
    suspend fun upsertMeal(meal: Meal)

    // Member Update
    @Update
    suspend fun updateMember(member: Member)

    // Deposit Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: Deposit)

    @Query("SELECT * FROM meals WHERE date = :date")
    fun getMealsForDate(date: String): Flow<List<Meal>>

    @Query("SELECT SUM(breakfast + lunch + dinner) FROM meals WHERE memberId = :memberId AND date LIKE :monthYear || '%'")
    fun getTotalMealsForMemberInMonth(memberId: Int, monthYear: String): Flow<Double?>

    @Query("SELECT * FROM deposits WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getDepositsForMember(memberId: Int): Flow<List<Deposit>>

    // Utility Bill Operations
    @Upsert
    suspend fun upsertUtilityBill(bill: UtilityBill)

    @Delete
    suspend fun deleteUtilityBill(bill: UtilityBill)

    @Query("SELECT * FROM utility_bills WHERE monthYear = :monthYear ORDER BY id DESC")
    fun getUtilityBillsForMonth(monthYear: String): Flow<List<UtilityBill>>

    @Query("SELECT SUM(amount) FROM utility_bills WHERE monthYear = :monthYear")
    fun getTotalUtilityCostForMonth(monthYear: String): Flow<Double?>
}