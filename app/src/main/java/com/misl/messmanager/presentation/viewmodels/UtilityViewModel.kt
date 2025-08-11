package com.misl.messmanager.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misl.messmanager.data.local.entity.UtilityBill
import com.misl.messmanager.domain.repository.MessRepository
import com.misl.messmanager.presentation.states.UtilityScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val repository: MessRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UtilityScreenState())
    val state = _state.asStateFlow()

    private val monthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    init {
        viewModelScope.launch {
            repository.getUtilityBillsForMonth(monthYear).collect { bills ->
                _state.update { it.copy(bills = bills) }
            }
        }
        viewModelScope.launch {
            repository.getTotalUtilityCostForMonth(monthYear).collect { total ->
                _state.update { it.copy(totalCost = total ?: 0.0) }
            }
        }
    }

    fun addBill(name: String, amount: Double) {
        if (name.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.saveUtilityBill(
                UtilityBill(monthYear = monthYear, name = name, amount = amount)
            )
        }
    }

    fun deleteBill(bill: UtilityBill) {
        viewModelScope.launch {
            repository.deleteUtilityBill(bill)
        }
    }
}