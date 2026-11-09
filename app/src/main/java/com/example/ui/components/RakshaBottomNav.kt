package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralEmergency
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.viewmodel.RakshaScreen

data class NavItem(
    val screen: RakshaScreen,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val isEmergency: Boolean = false
)

@Composable
fun RakshaBottomNav(
    currentScreen: RakshaScreen,
    onNavigate: (RakshaScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            screen = RakshaScreen.HOME,
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavItem(
            screen = RakshaScreen.CRIME_MAP,
            label = "Map",
            selectedIcon = Icons.Filled.Map,
            unselectedIcon = Icons.Outlined.Map
        ),
        NavItem(
            screen = RakshaScreen.SAFER_ROUTES,
            label = "Routes",
            selectedIcon = Icons.Filled.Navigation,
            unselectedIcon = Icons.Outlined.Navigation
        ),
        NavItem(
            screen = RakshaScreen.DIGITAL_FRAUD,
            label = "Fraud Shield",
            selectedIcon = Icons.Filled.Security,
            unselectedIcon = Icons.Outlined.Security
        ),
        NavItem(
            screen = RakshaScreen.EMERGENCY_SOS,
            label = "SOS Hub",
            selectedIcon = Icons.Filled.Warning,
            unselectedIcon = Icons.Outlined.Warning,
            isEmergency = true
        )
    )

    Box(
        modifier = modifier
            .background(RakshaNavy800)
            .border(width = 1.dp, color = SurfaceBorderDark)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val selected = (currentScreen == item.screen)
                val iconColor = if (item.isEmergency) CoralEmergency else CyanPrimaryLight

                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = if (selected) iconColor else Color(0xFF94A3B8)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            color = if (selected) (if (item.isEmergency) CoralEmergency else Color.White) else Color(0xFF64748B)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = if (item.isEmergency) CoralEmergency.copy(alpha = 0.2f) else CyanContainer,
                        selectedIconColor = iconColor,
                        unselectedIconColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_item_${item.screen.name.lowercase()}")
                )
            }
        }
    }
}
