package com.misl.messmanager.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
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
import com.misl.messmanager.presentation.states.MemberEvent
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
    var memberToDelete by remember { mutableStateOf<Member?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Members") },
                // The back arrow was moved inside the NavHost in previous examples.
                // If you want it here, this is fine. Otherwise, it can be removed.
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(MemberEvent.ShowAddMemberDialog) }) {
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
                    onClick = { viewModel.onEvent(MemberEvent.MemberClicked(member)) }
                )
            }
        }
    }

    if (state.isAddMemberDialogShown) {
        AddMemberDialog(
            onDismiss = {
                viewModel.onEvent(MemberEvent.DismissAllDialogs)
            },
            onConfirm = { name, contact, amount ->
                viewModel.onEvent(MemberEvent.ConfirmAddMember(name, contact, amount))
            }
        )
    }

    if (state.isDetailDialogShown && state.selectedMember != null) {
        MemberDetailDialog(
            state = state,
            onDismiss = { viewModel.onEvent(MemberEvent.DismissAllDialogs) },
            onAddDeposit = { viewModel.onEvent(MemberEvent.AddDeposit(it)) },
            onDelete = { memberToDelete = state.selectedMember }
        )
    }

    // The confirmation dialog logic remains the same.
    memberToDelete?.let { member ->
        ConfirmationDialog(
            title = "Delete Member",
            text = "Are you sure you want to delete ${member.name}? All of their data will be permanently removed.",
            onConfirm = {
                viewModel.onEvent(MemberEvent.DeleteMember(member))
                memberToDelete = null
            },
            onDismiss = {
                memberToDelete = null
            }
        )
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete")
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Member Icon",
                    modifier = Modifier.size(40.dp)
                )
            },
            headlineContent = {
                Text(
                    text = member.name,
                    fontWeight = FontWeight.SemiBold
                )
            },
            // The `supportingContent` now contains a Column for multiple lines
            supportingContent = {
                Column {
                    // Line 1: Total Deposit
                    Text(
                        text = "Total Deposit: %.2f Tk".format(member.givenAmount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // Line 2: Contact Info (only shows if it exists)
                    if (member.contact.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Contact",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = member.contact,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View Details"
                )
            }
        )
    }
}

@Composable
fun MemberDetailDialog(
    state: MemberScreenState,
    onDismiss: () -> Unit,
    onAddDeposit: (Double) -> Unit,
    onDelete: () -> Unit
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
                // Display the contact number with an icon
                state.selectedMember?.contact?.let { contactNumber ->
                    if (contactNumber.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Contact",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = contactNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
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
                // This is the new part for the action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete Button
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(Modifier.width(4.dp))
                        Text("Delete")
                    }
                    // Close Button
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
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