package com.misl.messmanager.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misl.messmanager.data.local.entity.Bazar
import com.misl.messmanager.domain.repository.MessRepository
import com.misl.messmanager.presentation.states.BazarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BazarViewModel @Inject constructor(
    private val repository: MessRepository
) : ViewModel() {

    // Get the current month in "YYYY-MM" format
    private val currentMonthYear: String
        get() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    // ✅ **THE CORRECT PATTERN**
    // Combine the source flows and convert them into a StateFlow.
    val state: StateFlow<BazarState> = repository.getBazarForMonth(currentMonthYear)
        .combine(repository.getTotalBazarForMonth(currentMonthYear)) { bazarList, total ->
            BazarState(
                bazarList = bazarList,
                totalBazar = total ?: 0.0
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BazarState() // Start with a loading state
        )


    fun addBazar(amount: Double, description: String) {
        if (amount <= 0 || description.isBlank()) {
            return
        }
        viewModelScope.launch {
            val newBazar = Bazar(
                amount = amount,
                description = description,
                timestamp = System.currentTimeMillis()
            )
            repository.addBazar(newBazar)
        }
    }
}