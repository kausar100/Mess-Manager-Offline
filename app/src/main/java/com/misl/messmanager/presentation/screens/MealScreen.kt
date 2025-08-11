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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.misl.messmanager.data.local.entity.Member
import com.misl.messmanager.domain.model.MealType
import com.misl.messmanager.presentation.viewmodels.MealViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(
    navController: NavController,
    viewModel: MealViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Daily Meal Entry") },  navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            DateSelector(
                selectedDate = state.selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")),
                onPreviousClick = { viewModel.changeDate(-1) },
                onNextClick = { viewModel.changeDate(1) }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.members) { member ->
                        MealEntryCard(
                            member = member,
                            getCheckedState = { mealType ->
                                val meal = state.mealsForDate[member.id]
                                when (mealType) {
                                    MealType.BREAKFAST -> meal?.breakfast ?: 0.0 > 0.0
                                    MealType.LUNCH -> meal?.lunch ?: 0.0 > 0.0
                                    MealType.DINNER -> meal?.dinner ?: 0.0 > 0.0
                                }
                            },
                            onMealToggle = { mealType, isChecked ->
                                viewModel.toggleMeal(member.id, mealType, isChecked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Previous Day")
        }
        Text(
            text = selectedDate,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextClick) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Next Day")
        }
    }
}

@Composable
fun MealEntryCard(
    member: Member,
    getCheckedState: (MealType) -> Boolean,
    onMealToggle: (MealType, Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = member.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CheckboxWithLabel(
                    label = "Breakfast (0.5)",
                    isChecked = getCheckedState(MealType.BREAKFAST),
                    onCheckedChange = { onMealToggle(MealType.BREAKFAST, it) }
                )
                CheckboxWithLabel(
                    label = "Lunch (1.0)",
                    isChecked = getCheckedState(MealType.LUNCH),
                    onCheckedChange = { onMealToggle(MealType.LUNCH, it) }
                )
                CheckboxWithLabel(
                    label = "Dinner (1.0)",
                    isChecked = getCheckedState(MealType.DINNER),
                    onCheckedChange = { onMealToggle(MealType.DINNER, it) }
                )
            }
        }
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp)
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}