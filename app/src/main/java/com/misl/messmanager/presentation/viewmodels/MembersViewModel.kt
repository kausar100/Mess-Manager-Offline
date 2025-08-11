package com.misl.messmanager.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.domain.repository.MessRepository
import com.misl.messmanager.presentation.states.MemberEvent
import com.misl.messmanager.presentation.states.MemberScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val repository: MessRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MemberScreenState())
    val state = _state.asStateFlow()

    private var depositJob: kotlinx.coroutines.Job? = null

    val members = repository.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getAllMembers().collect { members ->
                _state.update { it.copy(members = members) }
            }
        }
    }


    fun onEvent(event: MemberEvent) {
        when (event) {
            is MemberEvent.MemberClicked -> {
                _state.update { it.copy(selectedMember = event.member, isDetailDialogShown = true) }
                depositJob?.cancel()
                depositJob = viewModelScope.launch {
                    repository.getDepositsForMember(event.member.id).collect { deposits ->
                        _state.update { it.copy(depositsForSelectedMember = deposits) }
                    }
                }
            }
            is MemberEvent.AddDeposit -> {
                val member = _state.value.selectedMember ?: return
                if (event.amount <= 0) return
                viewModelScope.launch {
                    val newDeposit = Deposit(memberId = member.id, amount = event.amount)
                    repository.addDeposit(newDeposit)
                    val updatedMember = member.copy(givenAmount = member.givenAmount + event.amount)
                    repository.updateMember(updatedMember)
                }
            }
            is MemberEvent.ConfirmAddMember -> {
                if (event.name.isBlank() || event.amount <= 0) return
                viewModelScope.launch {
                    repository.addMemberWithInitialDeposit(event.name,event.phone, event.amount)
                }
                onEvent(MemberEvent.DismissAllDialogs)
            }
            is MemberEvent.ShowAddMemberDialog -> {
                _state.update { it.copy(isAddMemberDialogShown = true) }
            }
            is MemberEvent.DismissAllDialogs -> {
                depositJob?.cancel()
                _state.update {
                    it.copy(
                        isDetailDialogShown = false,
                        isAddMemberDialogShown = false,
                        selectedMember = null
                    )
                }
            }

            is MemberEvent.DeleteMember -> {
                viewModelScope.launch {
                    repository.deleteMember(event.member)
                }
                // Dismiss any open dialogs after deletion
                onEvent(MemberEvent.DismissAllDialogs)
            }
        }
    }
}