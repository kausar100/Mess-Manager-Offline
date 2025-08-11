package com.misl.messmanager.presentation.states

import java.time.YearMonth

// Represents the calculated report for a single member
data class MemberReport(
    val memberName: String,
    val totalMeal: Double,
    val mealCost: Double,
    val deposit: Double,
    val utilityShare: Double,
    val balance: Double // Positive -> Manager pays member, Negative -> Member pays manager
)

// A new class to hold the calculated report data
data class FinalReport(
    val totalBazar: Double,
    val totalMeals: Double,
    val mealRate: Double,
    val totalUtilityCost: Double,
    val memberReports: List<MemberReport>
)

// The main UI state class for the screen
data class SummaryState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true,
    val report: FinalReport? = null
)

// Events sent from the UI to the ViewModel
sealed class SummaryEvent {
    // Pass 1 to go to the next month, -1 for the previous month
    data class ChangeMonth(val amount: Long) : SummaryEvent()
}