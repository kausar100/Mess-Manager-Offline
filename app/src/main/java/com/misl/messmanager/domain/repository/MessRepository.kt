package com.misl.messmanager.domain.repository

import com.misl.messmanager.data.local.entity.Bazar
import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Meal
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.data.local.entity.UtilityBill
import kotlinx.coroutines.flow.Flow

interface MessRepository {
    // Member functions
    fun getAllMembers(): Flow<List<Member>>
    suspend fun addMember(member: Member) : Long
    suspend fun updateMember(member: Member)
    suspend fun deleteMember(member: Member)

    suspend fun addDeposit(deposit: Deposit)
    fun getDepositsForMember(memberId: Int): Flow<List<Deposit>>
    suspend fun addMemberWithInitialDeposit(name: String, contact: String, amount: Double)


    suspend fun saveUtilityBill(bill: UtilityBill)
    suspend fun deleteUtilityBill(bill: UtilityBill)
    fun getUtilityBillsForMonth(monthYear: String): Flow<List<UtilityBill>>
    fun getTotalUtilityCostForMonth(monthYear: String): Flow<Double?>

    // Bazar functions
    fun getBazarForMonth(monthYear: String): Flow<List<Bazar>>
    fun getTotalBazarForMonth(monthYear: String): Flow<Double?>
    suspend fun addBazar(bazar: Bazar)

    // Meal functions
    fun getMealsForDate(date: String): Flow<List<Meal>>
    fun getTotalMealsForMemberInMonth(memberId: Int, monthYear: String): Flow<Double?>
    suspend fun saveMeal(meal: Meal)
}