package com.misl.messmanager.presentation.states

import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Member

// Add a flag for the new dialog
data class MemberScreenState(
    val members: List<Member> = emptyList(),
    val selectedMember: Member? = null,
    val depositsForSelectedMember: List<Deposit> = emptyList(),
    val isDetailDialogShown: Boolean = false,
    val isAddMemberDialogShown: Boolean = false // <-- ADD THIS
)


sealed class MemberEvent {
    data class MemberClicked(val member: Member) : MemberEvent()
    data class AddDeposit(val amount: Double) : MemberEvent()
    data class ConfirmAddMember(val name: String, val phone: String, val amount: Double) : MemberEvent()
    data object ShowAddMemberDialog : MemberEvent()
    data object DismissAllDialogs : MemberEvent()
    data class DeleteMember(val member: Member) : MemberEvent()
}