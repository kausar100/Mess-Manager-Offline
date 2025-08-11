package com.misl.messmanager.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.misl.messmanager.data.local.entity.UtilityBill
import com.misl.messmanager.presentation.viewmodels.UtilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityScreen(
    navController: NavController,
    viewModel: UtilityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Utility Costs for August") }, actions = {
                IconButton(onClick = {
                    navController.popBackStack()
                }, Modifier.padding(end = 16.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back-button")
                }
            })
        }, // You can make the month dynamic
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Utility Bill")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            TotalCostCard(totalAmount = state.totalCost)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.bills) { bill ->
                    UtilityBillItem(
                        bill = bill,
                        onDelete = { viewModel.deleteBill(bill) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddBillDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount ->
                viewModel.addBill(name, amount)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TotalCostCard(totalAmount: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Utility Cost", style = MaterialTheme.typography.titleMedium)
            Text(
                "%.2f Tk".format(totalAmount),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UtilityBillItem(bill: UtilityBill, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                bill.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Text("%.2f".format(bill.amount), fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Bill", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddBillDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Utility Bill") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Bill Name (e.g., WiFi)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, amount.toDoubleOrNull() ?: 0.0) }) {
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