package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.CrimeRiskCluster
import com.example.data.model.HistoricalRiskLevel
import com.example.data.model.RouteOption
import com.example.data.model.RouteType
import com.example.data.repository.RoutingRepository
import com.example.ui.components.RealLifeMysuruMapView
import com.example.ui.components.RiskBadge
import com.example.ui.theme.AmberCaution
import com.example.ui.theme.CoralEmergency
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.viewmodel.RakshaUiState

@Composable
fun SaferRoutesScreen(
    uiState: RakshaUiState,
    clusters: List<CrimeRiskCluster>,
    destinationPresets: List<RoutingRepository.DestinationPreset>,
    onSelectRoute: (RouteOption) -> Unit,
    onStartJourney: () -> Unit,
    onStopJourney: () -> Unit,
    onTriggerSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPresetIndex by remember { mutableStateOf(0) }
    val currentPreset = destinationPresets.getOrNull(selectedPresetIndex) ?: destinationPresets.first()

    // Generate routes for selected destination and city with live OSRM support
    var routes by remember(selectedPresetIndex, uiState.selectedCity) {
        mutableStateOf(
            RoutingRepository().computeRouteOptions(
                originLat = uiState.userLat,
                originLng = uiState.userLng,
                destLat = currentPreset.destLat,
                destLng = currentPreset.destLng,
                city = uiState.selectedCity.cityName
            )
        )
    }
    var isRoutingLoading by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(selectedPresetIndex, uiState.selectedCity, uiState.userLat, uiState.userLng) {
        isRoutingLoading = true
        when (val result = RoutingRepository().requestOsrmRoutes(
            originLat = uiState.userLat,
            originLng = uiState.userLng,
            destLat = currentPreset.destLat,
            destLng = currentPreset.destLng,
            city = uiState.selectedCity.cityName
        )) {
            is com.example.data.repository.RouteCalculationResult.Success -> {
                if (result.routes.isNotEmpty()) {
                    routes = result.routes
                    onSelectRoute(result.routes.first())
                }
            }
            is com.example.data.repository.RouteCalculationResult.Error -> {
                if (result.fallbackRoutes.isNotEmpty()) {
                    routes = result.fallbackRoutes
                    onSelectRoute(result.fallbackRoutes.first())
                }
            }
        }
        isRoutingLoading = false
    }

    val activeRoute = uiState.selectedRoute ?: routes.firstOrNull()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Header
        Column {
            Text(
                text = "${uiState.selectedCity.cityName} Safer Routes",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "OpenStreetMap + OSRM Public Engine",
                    color = CyanPrimaryLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isRoutingLoading) "• Computing road coordinates..." else "• Ready",
                    color = if (isRoutingLoading) AmberCaution else EmeraldVerified,
                    fontSize = 11.sp
                )
            }
        }

        // DESTINATION SELECTOR CHIPS
        Text(
            text = "SELECT DESTINATION (${uiState.selectedCity.cityName.uppercase()})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(destinationPresets.size) { index ->
                val preset = destinationPresets[index]
                val isSelected = (index == selectedPresetIndex)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyanPrimaryLight.copy(alpha = 0.2f) else RakshaNavy800)
                        .border(
                            1.dp,
                            if (isSelected) CyanPrimaryLight else SurfaceBorderDark,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedPresetIndex = index
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("destination_preset_$index")
                ) {
                    Column {
                        Text(
                            text = preset.name,
                            color = if (isSelected) CyanPrimaryLight else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = preset.subtitle,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Real-Life Embedded Interactive Map with Route Polyline
        RealLifeMysuruMapView(
            userLat = uiState.userLat,
            userLng = uiState.userLng,
            userAccuracyMeters = uiState.userGpsAccuracy,
            isRealGps = uiState.isRealGpsActive,
            areas = uiState.mysuruAreas,
            clusters = clusters,
            emergencyPlaces = emptyList(),
            selectedRoute = activeRoute,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        // ACTIVE JOURNEY ESCORT CONTROLS
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isJourneyActive) EmeraldContainer.copy(alpha = 0.4f) else RakshaNavy800
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (uiState.isJourneyActive) EmeraldVerified else SurfaceBorderDark,
                    RoundedCornerShape(14.dp)
                )
                .testTag("journey_escort_card")
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
                                .background(if (uiState.isJourneyActive) EmeraldVerified else Color(0xFF64748B))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isJourneyActive) "JOURNEY ESCORT ACTIVE" else "JOURNEY ESCORT READY",
                            color = if (uiState.isJourneyActive) EmeraldVerified else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    if (uiState.isJourneyActive) {
                        Text(
                            text = "${uiState.journeyElapsedSeconds / 60}m ${uiState.journeyElapsedSeconds % 60}s elapsed",
                            color = Color(0xFFD1FAE5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (uiState.isJourneyActive)
                        "Tracking route safety. If safety check-in is not confirmed after ETA, emergency contacts will be prompted."
                    else
                        "Activate Escort Mode to monitor your progress and maintain instant SOS access throughout your commute.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!uiState.isJourneyActive) {
                        Button(
                            onClick = onStartJourney,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldVerified),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("start_journey_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Journey Escort", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    } else {
                        Button(
                            onClick = onStopJourney,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("stop_journey_button")
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete Journey", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onTriggerSos,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralEmergency),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .border(1.dp, CoralEmergency, RoundedCornerShape(10.dp))
                            .testTag("journey_quick_sos_button")
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SOS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // CANDIDATE ROUTE CARDS
        Text(
            text = "EVALUATED ROUTE OPTIONS & TRADEOFFS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        routes.forEach { route ->
            val isSelected = (activeRoute?.routeId == route.routeId)
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF1E2E4A) else RakshaNavy800),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isSelected) CyanPrimaryLight else SurfaceBorderDark,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectRoute(route) }
                    .testTag("route_option_${route.routeId.lowercase()}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = route.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${route.distanceKm} km • ~${route.etaMinutes} mins walking",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                        RiskBadge(level = route.historicalRiskLevel, score = route.exposureScore)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Advantages
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        route.safetyAdvantages.forEach { adv ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldVerified,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = adv, color = Color(0xFFE2E8F0), fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tradeoffs Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Factors & Tradeoffs:",
                                color = CyanPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = route.tradeoffs,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lighting / Surveillance: ${route.infrastructureSummary}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isSelected) {
                        Button(
                            onClick = {
                                if (!uiState.isJourneyActive) {
                                    onStartJourney()
                                } else {
                                    onStopJourney()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isJourneyActive) EmeraldVerified else CyanPrimaryLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toggle_in_app_escort")
                        ) {
                            Icon(
                                imageVector = if (uiState.isJourneyActive) Icons.Default.CheckCircle else Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = RakshaNavy900
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isJourneyActive) "In-App Escort Active (Tracking Route)" else "Start In-App Live Route Escort",
                                color = RakshaNavy900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // TRANSPARENT TELEMETRY DISCLAIMER
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(10.dp))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CyanPrimaryLight, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pedestrian crowd density is not reported due to the absence of genuine live sensor telemetry. Crime-risk exposure is evaluated strictly from documented municipal incident logs (2021–2024).",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
