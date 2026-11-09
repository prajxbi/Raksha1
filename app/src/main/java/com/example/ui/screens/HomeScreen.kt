package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.LocationHelper
import com.example.data.model.HistoricalRiskLevel
import com.example.ui.components.RiskBadge
import com.example.ui.theme.AmberCaution
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralEmergency
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy700
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.viewmodel.RakshaScreen
import com.example.ui.viewmodel.RakshaUiState

@Composable
fun HomeScreen(
    uiState: RakshaUiState,
    contactsCount: Int,
    onNavigate: (RakshaScreen) -> Unit,
    onTriggerSos: () -> Unit,
    onCancelSos: () -> Unit,
    onRefreshLocation: () -> Unit,
    onDismissProximityAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. ACTIVE SOS COUNTDOWN OVERLAY / CARD (if triggered)
        AnimatedVisibility(
            visible = uiState.isSosCountdownActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CoralContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CoralEmergency, RoundedCornerShape(16.dp))
                    .testTag("sos_countdown_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMERGENCY SOS TRIGGERED",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Dispatching location coordinates to $contactsCount contacts in:",
                        color = Color(0xFFFECACA),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${uiState.sosCountdownRemaining}",
                        color = Color.White,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCancelSos,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = CoralEmergency
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("cancel_sos_button")
                    ) {
                        Text(
                            text = "CANCEL (FALSE ALARM)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Status dispatch feedback message
        if (uiState.latestSosDispatchStatus != null && !uiState.isSosCountdownActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .border(1.dp, CyanPrimaryLight.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
                    .testTag("sos_status_banner")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Status",
                        tint = CyanPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.latestSosDispatchStatus,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. LOCATION & CRIME-RISK STATUS CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
                .testTag("location_status_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EmeraldVerified)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isRealGpsActive) "LIVE HARDWARE GPS (±${uiState.userGpsAccuracy.toInt()}m)" else "CURRENT LOCATION (MYSURU)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = if (uiState.isRealGpsActive) EmeraldVerified else CyanPrimaryLight
                        )
                    }

                    IconButton(
                        onClick = onRefreshLocation,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("refresh_location_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh GPS Location",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Pin",
                        tint = CoralEmergency,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.userAddress,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Factual Risk Tier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Historical Incident Exposure:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        RiskBadge(
                            level = uiState.locationRiskLevel,
                            score = uiState.locationRiskScore
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigate(RakshaScreen.CRIME_MAP) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimaryLight),
                        modifier = Modifier.testTag("view_map_btn")
                    ) {
                        Text("Explore Map", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiState.locationRiskLevel.disclaimer,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // 3. PROXIMITY ALERT BANNER (If near an elevated cluster and not dismissed)
        if (!uiState.proximityAlertDismissed && uiState.locationRiskLevel == HistoricalRiskLevel.ELEVATED) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AmberContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .border(1.dp, AmberCaution.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .testTag("proximity_alert_banner")
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Caution",
                        tint = AmberCaution,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Safety Awareness Notice",
                            color = AmberCaution,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                val alertText = if (uiState.selectedCity.cityName == "Mysuru") {
                    "You are currently situated within 400m of the Suburban Bus Stand & Nazarbad cluster where elevated late-night luggage theft and transit tout disputes have been logged. Keep bags in front."
                } else {
                    "You are currently situated within 400m of the 6th St & Market cluster where elevated historical property and street incidents have been logged. Keep valuables in zipped pockets."
                }
                Text(
                    text = alertText,
                    color = Color(0xFFFEF3C7),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            IconButton(
                onClick = onDismissProximityAlert,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = AmberCaution,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

        // 4. BIG EMERGENCY SOS BUTTON
        Card(
            colors = CardDefaults.cardColors(containerColor = CoralContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CoralEmergency.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .clickable { onTriggerSos() }
                .testTag("home_sos_trigger_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CoralEmergency),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "SOS",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EMERGENCY SOS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "5-second countdown • Sends GPS link to $contactsCount contacts & dials ${uiState.selectedCity.emergencyNumber}",
                        color = Color(0xFFFECACA),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 5. FOUR CORE MODULES QUICK ACCESS
        Text(
            text = "SAFETY MODULES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF94A3B8)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Safety Map Card
            ModuleGridCard(
                title = "Safety Map",
                subtitle = "${uiState.selectedCity.cityName} risk density",
                icon = Icons.Default.Map,
                tint = CyanPrimaryLight,
                onClick = { onNavigate(RakshaScreen.CRIME_MAP) },
                modifier = Modifier.weight(1f),
                testTag = "home_card_safety_map"
            )

            // Safer Routes Card
            ModuleGridCard(
                title = "Safer Routes",
                subtitle = if (uiState.isJourneyActive) "Escort Active (${uiState.journeyElapsedSeconds}s)" else "Compare route tradeoffs",
                icon = Icons.Default.Navigation,
                tint = if (uiState.isJourneyActive) EmeraldVerified else Color(0xFF38BDF8),
                onClick = { onNavigate(RakshaScreen.SAFER_ROUTES) },
                modifier = Modifier.weight(1f),
                testTag = "home_card_safer_routes"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Digital Fraud Shield Card
            ModuleGridCard(
                title = "Fraud Shield",
                subtitle = "SMS, URL & Caller check",
                icon = Icons.Default.Security,
                tint = Color(0xFFA855F7),
                onClick = { onNavigate(RakshaScreen.DIGITAL_FRAUD) },
                modifier = Modifier.weight(1f),
                testTag = "home_card_fraud_shield"
            )

            // Emergency Contacts Card
            ModuleGridCard(
                title = "SOS Hub & Contacts",
                subtitle = "$contactsCount registered contacts",
                icon = Icons.Default.ContactPhone,
                tint = CoralEmergency,
                onClick = { onNavigate(RakshaScreen.EMERGENCY_SOS) },
                modifier = Modifier.weight(1f),
                testTag = "home_card_emergency_sos"
            )
        }

        // 6. IMMEDIATE EMERGENCY DIAL SHORTCUTS
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DIRECT EMERGENCY SHORTCUTS (${uiState.selectedCity.cityName.uppercase()})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { LocationHelper.launchEmergencyDial(context, uiState.selectedCity.emergencyNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_dial_police_button")
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call ${uiState.selectedCity.emergencyNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { LocationHelper.launchEmergencyDial(context, uiState.selectedCity.ambulanceNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_dial_ambulance_button")
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ambulance ${uiState.selectedCity.ambulanceNumber}", fontSize = 11.sp)
                    }
                }
            }
        }

        // 7. FACTUAL PROVENANCE DISCLAIMER FOOTER
        Text(
            text = "Data Provenance: Historical incident density calculated from official municipal police department records (2021–2024). This analysis conveys historical report concentration and does NOT guarantee safety or forecast future incidents. Always adhere to official emergency directives.",
            fontSize = 10.sp,
            lineHeight = 14.sp,
            color = Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ModuleGridCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}
