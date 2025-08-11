package com.misl.messmanager.presentation.states

import android.os.Build
import androidx.annotation.RequiresApi
import com.misl.messmanager.data.local.entity.Meal
import com.misl.messmanager.data.local.entity.Member
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
data class MealScreenState  constructor(
    val selectedDate: LocalDate = LocalDate.now(),
    val members: List<Member> = emptyList(),
    // Map for fast lookups: Member ID -> Meal object
    val mealsForDate: Map<Int, Meal> = emptyMap(),
    val isLoading: Boolean = false
)