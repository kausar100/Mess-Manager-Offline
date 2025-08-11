package com.misl.messmanager.presentation.states

import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Member

data class MemberScreenState(
    val members: List<Member> = emptyList(),
    val selectedMember: Member? = null,
    val depositsForSelectedMember: List<Deposit> = emptyList(),
    val isDetailDialogShown: Boolean = false
)
