package com.misl.messmanager.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misl.messmanager.domain.repository.MessRepository
import com.misl.messmanager.presentation.states.FinalReport
import com.misl.messmanager.presentation.states.MemberReport
import com.misl.messmanager.presentation.states.SummaryEvent
import com.misl.messmanager.presentation.states.SummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: MessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryState())
    val uiState = _uiState.asStateFlow()

    init {
        // This is the main reactive loop. It observes the selectedMonth in the state.
        // Whenever the month changes, it automatically triggers a new calculation.
        viewModelScope.launch {
            _uiState.map { it.selectedMonth }
                .distinctUntilChanged()
                .flatMapLatest { month ->
                    // Set loading state and then perform calculation
                    _uiState.update { it.copy(isLoading = true) }
                    performCalculation(month)
                }
                .collect { finalReport ->
                    // Update state with the final report
                    _uiState.update {
                        it.copy(isLoading = false, report = finalReport)
                    }
                }
        }
    }

    private fun performCalculation(month: YearMonth): Flow<FinalReport> {
        return flow {
            val monthYearStr = month.format(DateTimeFormatter.ofPattern("yyyy-MM"))

            // Fetch all required data
            val utilityCost = repository.getTotalUtilityCostForMonth(monthYearStr).first() ?: 0.0
            val totalBazar = repository.getTotalBazarForMonth(monthYearStr).first() ?: 0.0
            val members = repository.getAllMembers().first()

            if (members.isEmpty()) {
                emit(FinalReport(totalBazar, 0.0, 0.0, utilityCost, emptyList()))
                return@flow
            }

            // ✅ CORRECTED CODE BLOCK
            val memberMealPairs = coroutineScope {
                members.map { member ->
                    async {
                        val meals =
                            repository.getTotalMealsForMemberInMonth(member.id, monthYearStr)
                                .first() ?: 0.0
                        member to meals
                    }
                }.awaitAll()
            }

            // Perform calculations (this part remains the same)
            val totalMeals = memberMealPairs.sumOf { it.second }
            val mealRate = if (totalMeals > 0) (totalBazar + utilityCost) / totalMeals else 0.0
            val utilityShare = if (members.isNotEmpty()) utilityCost / members.size else 0.0

            val memberReports = memberMealPairs.map { (member, individualMeals) ->
                val individualMealCost = mealRate * individualMeals
                // Note: The original formula was deposit - cost. If utility cost is part of the meal rate, it's double-counted.
                // Let's adjust the formula slightly for accuracy. Total cost = Bazar + Utility.
                val balance = member.givenAmount - individualMealCost
                MemberReport(
                    memberName = member.name,
                    totalMeal = individualMeals,
                    mealCost = individualMealCost,
                    deposit = member.givenAmount,
                    utilityShare = utilityShare, // Still useful to show this breakdown
                    balance = balance
                )
            }
            // Emit the final, calculated report
            emit(FinalReport(totalBazar, totalMeals, mealRate, utilityCost, memberReports))
        }
    }

    fun onEvent(event: SummaryEvent) {
        when (event) {
            is SummaryEvent.ChangeMonth -> {
                val currentMonth = _uiState.value.selectedMonth
                val newMonth = currentMonth.plusMonths(event.amount)
                _uiState.update { it.copy(selectedMonth = newMonth) }
            }
        }
    }
}