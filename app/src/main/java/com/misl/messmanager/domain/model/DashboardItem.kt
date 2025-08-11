package com.misl.messmanager.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

// Data class to hold info for each dashboard item
data class DashboardItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// List of dashboard items
val dashboardItems = listOf(
    DashboardItem("Manage Members", Icons.Default.Person, "members"),
    DashboardItem("Daily Meals", Icons.Default.Menu, "meals"),
    DashboardItem("Bazar List", Icons.Default.ShoppingCart, "bazar"),
    DashboardItem("Utility Costs", Icons.Default.Build, "utilities"), // Add this line
)