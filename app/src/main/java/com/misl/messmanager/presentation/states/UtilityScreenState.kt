package com.misl.messmanager.presentation.states

import com.misl.messmanager.data.local.entity.UtilityBill

data class UtilityScreenState(
    val bills: List<UtilityBill> = emptyList(),
    val totalCost: Double = 0.0
)