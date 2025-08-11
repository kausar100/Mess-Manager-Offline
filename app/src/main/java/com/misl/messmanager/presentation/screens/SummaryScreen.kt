package com.misl.messmanager.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.misl.messmanager.presentation.states.FinalReport
import com.misl.messmanager.presentation.states.MemberReport
import com.misl.messmanager.presentation.states.SummaryEvent
import com.misl.messmanager.presentation.viewmodels.SummaryViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navController: NavController,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Monthly Summary") }, actions = {
                IconButton(onClick = {
                    navController.popBackStack()
                }, Modifier.padding(end = 16.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back-button")
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            MonthSelector(
                selectedMonth = state.selectedMonth,
                onMonthChange = { viewModel.onEvent(SummaryEvent.ChangeMonth(it)) }
            )
            Divider()

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else if (state.report != null) {
                    SummaryReport(report = state.report!!)
                } else {
                    Text("No data available for this month.")
                }
            }
        }
    }
}

@Composable
fun MonthSelector(
    selectedMonth: YearMonth,
    onMonthChange: (Long) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onMonthChange(-1) }) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Previous Month",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f)
            )
        }
        Text(
            text = selectedMonth.format(formatter),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { onMonthChange(1) }) {
            Icon(Icons.Default.Send, contentDescription = "Next Month")
        }
    }
}


@Composable
fun SummaryReport(report: FinalReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            OverallStatsCard(report = report)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Member Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(report.memberReports) { memberReport ->
            MemberReportCard(report = memberReport)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// The following Composables (OverallStatsCard, MemberReportCard, etc.)
// remain the same as the previous versions.

@Composable
fun OverallStatsCard(report: FinalReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Overall Statistics", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Total Bazar", "%.2f Tk".format(report.totalBazar))
                StatItem("Utilities", "%.2f Tk".format(report.totalUtilityCost))
                StatItem("Meal Rate", "%.2f".format(report.mealRate))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MemberReportCard(report: MemberReport) {
    val balanceColor = if (report.balance >= 0) Color(0xFF008000) else Color.Red
    val balanceText = if (report.balance >= 0) "Will Get" else "Will Give"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(report.memberName, style = MaterialTheme.typography.titleLarge)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ReportRow("Deposit:", "%.2f Tk".format(report.deposit))
            ReportRow("Total Meals:", "%.1f".format(report.totalMeal))
            ReportRow("Meal Cost:", "(-%.2f Tk)".format(report.mealCost))
            ReportRow("Utility Share:", "(-%.2f Tk)".format(report.utilityShare))
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = balanceText,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = balanceColor
                )
                Text(
                    text = "%.2f Tk".format(abs(report.balance)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = balanceColor
                )
            }
        }
    }
}

@Composable
fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}