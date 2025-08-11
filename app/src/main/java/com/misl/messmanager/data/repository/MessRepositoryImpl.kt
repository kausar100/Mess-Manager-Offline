package com.misl.messmanager.data.repository

import com.misl.messmanager.data.local.dao.MessDao
import com.misl.messmanager.data.local.entity.Bazar
import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Meal
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.data.local.entity.UtilityBill
import com.misl.messmanager.domain.repository.MessRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessRepositoryImpl @Inject constructor(
    private val dao: MessDao
) : MessRepository {
    // Implement all the functions from the interface by calling the corresponding DAO methods
    override fun getAllMembers(): Flow<List<Member>> = dao.getAllMembers()
    override suspend fun addMember(member: Member): Long = dao.insertMember(member)

    override suspend fun addMemberWithInitialDeposit(name: String, contact: String, amount: Double) {
        val member = Member(name = name, contact = contact, givenAmount = amount)
        val deposit = Deposit(memberId = 0, amount = amount, description = "Initial Deposit") // memberId is a placeholder
        dao.addMemberWithInitialDeposit(member, deposit)
    }

    override suspend fun deleteMember(member: Member) = dao.deleteMember(member)

    override suspend fun deleteBazar(bazar: Bazar) = dao.deleteBazar(bazar)


    override suspend fun updateMember(member: Member) = dao.updateMember(member)
    override suspend fun addDeposit(deposit: Deposit) = dao.insertDeposit(deposit)
    override fun getDepositsForMember(memberId: Int): Flow<List<Deposit>> = dao.getDepositsForMember(memberId)

    // ... implement all other functions similarly
    override fun getBazarForMonth(monthYear: String): Flow<List<Bazar>> = dao.getBazarForMonth(monthYear)
    override fun getTotalBazarForMonth(monthYear: String): Flow<Double?> = dao.getTotalBazarForMonth(monthYear)
    override suspend fun addBazar(bazar: Bazar) = dao.insertBazar(bazar)

    override fun getMealsForDate(date: String): Flow<List<Meal>> = dao.getMealsForDate(date)
    override fun getTotalMealsForMemberInMonth(memberId: Int, monthYear: String): Flow<Double?> = dao.getTotalMealsForMemberInMonth(memberId, monthYear)
    override suspend fun saveMeal(meal: Meal) = dao.upsertMeal(meal)

    override suspend fun saveUtilityBill(bill: UtilityBill) = dao.upsertUtilityBill(bill)
    override suspend fun deleteUtilityBill(bill: UtilityBill) = dao.deleteUtilityBill(bill)
    override fun getUtilityBillsForMonth(monthYear: String): Flow<List<UtilityBill>> = dao.getUtilityBillsForMonth(monthYear)
    override fun getTotalUtilityCostForMonth(monthYear: String): Flow<Double?> = dao.getTotalUtilityCostForMonth(monthYear)

}