package com.misl.messmanager.presentation.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misl.messmanager.data.local.entity.Meal
import com.misl.messmanager.domain.model.MealType
import com.misl.messmanager.domain.repository.MessRepository
import com.misl.messmanager.presentation.states.MealScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MealViewModel @Inject constructor(
    private val repository: MessRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MealScreenState())
    val state = _state.asStateFlow()

    // Formatter for storing date in DB as "YYYY-MM-DD"

    private val dbDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        loadDataForDate(LocalDate.now())
    }

    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, selectedDate = date)

            val dateString = date.format(dbDateFormatter)

            // Flow for all members
            val membersFlow = repository.getAllMembers()
            // Flow for meals on the selected date
            val mealsFlow = repository.getMealsForDate(dateString)

            membersFlow.combine(mealsFlow) { members, meals ->
                _state.value = MealScreenState(
                    selectedDate = date,
                    members = members,
                    mealsForDate = meals.associateBy { it.memberId }, // Create a map for efficient lookups
                    isLoading = false
                )
            }.collect()
        }
    }

    fun toggleMeal(memberId: Int, mealType: MealType, isChecked: Boolean) {
        viewModelScope.launch {
            val currentState = _state.value
            val dateString = currentState.selectedDate.format(dbDateFormatter)

            // Find existing meal or create a new one
            val existingMeal =
                currentState.mealsForDate[memberId] ?: Meal(memberId = memberId, date = dateString)

            val updatedMeal = when (mealType) {
                MealType.BREAKFAST -> existingMeal.copy(breakfast = if (isChecked) mealType.value else 0.0)
                MealType.LUNCH -> existingMeal.copy(lunch = if (isChecked) mealType.value else 0.0)
                MealType.DINNER -> existingMeal.copy(dinner = if (isChecked) mealType.value else 0.0)
            }

            // Upsert (insert or update) the meal record
            repository.saveMeal(updatedMeal)
        }
    }

    fun changeDate(days: Long) {
        val newDate = _state.value.selectedDate.plusDays(days)
        loadDataForDate(newDate)
    }
}