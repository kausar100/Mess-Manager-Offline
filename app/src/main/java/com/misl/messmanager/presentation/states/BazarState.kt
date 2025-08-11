package com.misl.messmanager.presentation.states

import com.misl.messmanager.data.local.entity.Bazar

data class BazarState(
    val bazarList: List<Bazar> = emptyList(),
    val totalBazar: Double = 0.0,
    val isLoading: Boolean = false
)