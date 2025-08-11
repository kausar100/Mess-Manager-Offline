package com.misl.messmanager.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.misl.messmanager.data.local.entity.Deposit
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.presentation.states.MemberScreenState
import com.misl.messmanager.presentation.viewmodels.MembersViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberScreen(
    navController: NavController,
    viewModel: MembersViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val members by viewModel.members.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    // Note: The "Add Member" dialog from your previous code would be launched separately
    // This example focuses on the new "Member Details" dialog
    Scaffold(
        topBar = { TopAppBar(title = { Text("Members") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                 Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.members) { member ->
                MemberItem(
                    member = member,
                    onClick = { viewModel.onMemberClick(member) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddMemberDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, deposit ->
                viewModel.addMember(name, phone, deposit)
                showAddDialog = false
            }
        )
    }

    if (state.isDetailDialogShown) {
        MemberDetailDialog(
            state = state,
            onDismiss = viewModel::onDismissDialog,
            onAddDeposit = viewModel::addDeposit
        )
    }
}


@Composable
fun AddMemberDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var deposit by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Member") },
        text = {
            Column {
                OutlinedTextField(value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions(
                        // 2. Set the keyboard action to "Next"
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        // 3. Move focus to the next element when "Next" is pressed
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Phone number") },
                    keyboardOptions = KeyboardOptions(
                        // 2. Set the keyboard action to "Next"
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        // 3. Move focus to the next element when "Next" is pressed
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deposit,
                    onValueChange = { deposit = it },
                    label = { Text("Initial Deposit") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        // On the last field, use "Done"
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        // When "Done" is pressed, clear focus to hide the keyboard
                        onDone = { focusManager.clearFocus() }
                    ))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, contact, deposit.toDoubleOrNull() ?: 0.0) }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MemberItem(member: Member, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = member.name, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Total Deposit: %.2f Tk".format(member.givenAmount),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MemberDetailDialog(
    state: MemberScreenState,
    onDismiss: () -> Unit,
    onAddDeposit: (Double) -> Unit
) {
    var depositAmount by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    state.selectedMember?.name ?: "",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Total Given: %.2f Tk".format(state.selectedMember?.givenAmount ?: 0.0),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Deposit History List
                Text("Deposit History", style = MaterialTheme.typography.titleMedium)
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(state.depositsForSelectedMember) { deposit ->
                        DepositItem(deposit)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Add New Deposit Section
                Text("Add New Deposit", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onAddDeposit(depositAmount.toDoubleOrNull() ?: 0.0)
                        depositAmount = "" // Clear field after adding
                        focusManager.clearFocus()
                    }) {
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun DepositItem(deposit: Deposit) {
    val formattedDate =
        SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(deposit.timestamp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(formattedDate, style = MaterialTheme.typography.bodyMedium)
        Text(
            "%.2f Tk".format(deposit.amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}