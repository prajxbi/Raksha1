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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.data.location.LocationHelper
import com.example.data.model.CrimeIncident
import com.example.data.model.CrimeRiskCluster
import com.example.data.model.EmergencyPlace
import com.example.data.model.HistoricalRiskLevel
import com.example.data.model.MysuruArea
import com.example.data.model.PlaceType
import com.example.ui.components.InteractiveMapView
import com.example.ui.components.RealLifeMysuruMapView
import com.example.ui.components.RiskBadge
import com.example.ui.theme.AmberCaution
import com.example.ui.theme.CoralEmergency
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy700
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.viewmodel.RakshaUiState

enum class MapViewMode {
    REAL_LIFE_STREETS,
    TACTICAL_RADAR
}

@Composable
fun CrimeMapScreen(
    uiState: RakshaUiState,
    clusters: List<CrimeRiskCluster>,
    incidents: List<CrimeIncident>,
    emergencyPlaces: List<EmergencyPlace>,
    onSelectCluster: (CrimeRiskCluster?) -> Unit,
    onSelectIncident: (CrimeIncident?) -> Unit,
    onSelectArea: (MysuruArea?) -> Unit,
    onRecenterUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    var viewMode by remember { mutableStateOf(MapViewMode.REAL_LIFE_STREETS) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var focusedMapLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Filter areas based on user query
    val matchingAreas = remember(searchQuery, uiState.mysuruAreas) {
        if (searchQuery.isBlank()) {
            uiState.mysuruAreas
        } else {
            val q = searchQuery.trim().lowercase()
            uiState.mysuruAreas.filter {
                it.name.lowercase().contains(q) ||
                it.kannadaName.contains(q) ||
                it.category.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Google Maps-Style Top Search & Area Finder Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isSearchExpanded = it.isNotBlank()
            },
            placeholder = {
                Text(
                    text = "Search Mysuru areas (Gokulam, Palace, Vijayanagar...)",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CyanPrimaryLight
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        isSearchExpanded = false
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RakshaNavy800,
                unfocusedContainerColor = RakshaNavy800,
                focusedBorderColor = CyanPrimaryLight,
                unfocusedBorderColor = SurfaceBorderDark,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("map_area_search_bar")
        )

        // Dropdown Search Results (if user is actively typing)
        AnimatedVisibility(visible = isSearchExpanded && matchingAreas.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Mysuru Areas (${matchingAreas.size} found)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    matchingAreas.take(5).forEach { area ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSelectArea(area)
                                    searchQuery = area.name
                                    isSearchExpanded = false
                                    focusManager.clearFocus()
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = area.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${area.kannadaName} • ${area.category}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            RiskBadge(level = area.riskLevel)
                        }
                    }
                }
            }
        }

        // 2. Quick-Jump Mysuru Area Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Mysuru Neighborhoods (${uiState.mysuruAreas.size} areas)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.mysuruAreas) { area ->
                    val isSelected = (uiState.selectedMysuruArea?.id == area.id)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onSelectArea(null) else onSelectArea(area)
                        },
                        label = {
                            Text(
                                text = "${area.name.split(" ")[0]} (${area.kannadaName})",
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanPrimaryLight.copy(alpha = 0.25f),
                            selectedLabelColor = CyanPrimaryLight,
                            containerColor = RakshaNavy800,
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyanPrimaryLight else SurfaceBorderDark
                        )
                    )
                }
            }
        }

        // 3. Map View Mode Selector (Real-Life Google Map vs Tactical Radar)
        TabRow(
            selectedTabIndex = viewMode.ordinal,
            containerColor = RakshaNavy800,
            contentColor = CyanPrimaryLight,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[viewMode.ordinal]),
                    color = CyanPrimaryLight,
                    height = 2.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = viewMode == MapViewMode.REAL_LIFE_STREETS,
                onClick = { viewMode = MapViewMode.REAL_LIFE_STREETS },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Real-Life Map (Mysuru)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = viewMode == MapViewMode.TACTICAL_RADAR,
                onClick = { viewMode = MapViewMode.TACTICAL_RADAR },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Tactical Radar Canvas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // 4. THE INTERACTIVE MAP (360dp height for generous viewing)
        if (viewMode == MapViewMode.REAL_LIFE_STREETS) {
            RealLifeMysuruMapView(
                userLat = uiState.userLat,
                userLng = uiState.userLng,
                userAccuracyMeters = uiState.userGpsAccuracy,
                isRealGps = uiState.isRealGpsActive,
                areas = uiState.mysuruAreas,
                clusters = clusters,
                emergencyPlaces = emergencyPlaces,
                selectedArea = uiState.selectedMysuruArea,
                focusedLocation = focusedMapLocation,
                onSelectArea = { area -> onSelectArea(area) },
                onLocateMe = onRecenterUser,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            )
        } else {
            InteractiveMapView(
                userLat = uiState.userLat,
                userLng = uiState.userLng,
                clusters = clusters,
                incidents = incidents,
                emergencyPlaces = emergencyPlaces,
                cityName = uiState.selectedCity.cityName,
                selectedRoute = null,
                onClusterSelected = { cluster -> onSelectCluster(cluster) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            )
        }

        // 5. Real-Time GPS Telemetry & Coordinates Card
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
                .testTag("gps_telemetry_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isRealGpsActive) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (uiState.isRealGpsActive) EmeraldVerified else CyanPrimaryLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (uiState.isRealGpsActive) "Live Hardware GPS Signal" else "Mysuru Civic Reference",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Surface(
                        color = if (uiState.isRealGpsActive) EmeraldVerified.copy(alpha = 0.2f) else CyanPrimaryLight.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (uiState.isRealGpsActive) "±${uiState.userGpsAccuracy.toInt()}m Accuracy" else "Mysuru Hub",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isRealGpsActive) EmeraldVerified else CyanPrimaryLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = uiState.userAddress,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lat: ${"%.4f".format(uiState.userLat)}, Lng: ${"%.4f".format(uiState.userLng)}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Historical Risk:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        RiskBadge(level = uiState.locationRiskLevel)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRecenterUser,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimaryLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = RakshaNavy900,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Acquire Real GPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RakshaNavy900
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            focusedMapLocation = Pair(uiState.userLat, uiState.userLng)
                            viewMode = MapViewMode.REAL_LIFE_STREETS
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = CyanPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Center Map on GPS", fontSize = 11.sp)
                    }
                }
            }
        }

        // 6. Selected Mysuru Area Details Sheet
        AnimatedVisibility(visible = uiState.selectedMysuruArea != null) {
            val area = uiState.selectedMysuruArea
            if (area != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyanPrimaryLight.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = area.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${area.kannadaName} • ${area.category}",
                                    fontSize = 12.sp,
                                    color = CyanPrimaryLight
                                )
                            }
                            IconButton(onClick = { onSelectArea(null) }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }

                        Text(
                            text = area.description,
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 18.sp
                        )

                        // Safety Score Progress
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RakshaNavy900, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Area Safety Index",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${area.safetyScore} / 100",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (area.safetyScore >= 80) EmeraldVerified else AmberCaution
                                )
                            }
                            RiskBadge(level = area.riskLevel)
                        }

                        // Infrastructure Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = RakshaNavy900,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Lightbulb, null, tint = AmberCaution, modifier = Modifier.size(16.dp))
                                    Text(area.streetLightingRating, fontSize = 10.sp, color = Color.White)
                                }
                            }
                            Surface(
                                color = RakshaNavy900,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Videocam, null, tint = CyanPrimaryLight, modifier = Modifier.size(16.dp))
                                    Text(area.cctvCoverageRating, fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }

                        // Police Jurisdiction
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RakshaNavy900, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Jurisdiction Police Station:", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(area.nearestPoliceStation, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Button(
                                onClick = { LocationHelper.launchEmergencyDial(context, area.policePhone) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimaryLight),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Call, null, tint = RakshaNavy900, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call PS", fontSize = 11.sp, color = RakshaNavy900, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 7. Selected Crime Cluster Details Sheet
        AnimatedVisibility(visible = uiState.selectedCluster != null) {
            val cluster = uiState.selectedCluster
            if (cluster != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
                        .testTag("cluster_details_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = cluster.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Cluster ID: ${cluster.clusterId}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            IconButton(onClick = { onSelectCluster(null) }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }

                        RiskBadge(level = cluster.riskLevel)

                        Text(
                            text = "Predominant Offenses: ${cluster.predominantOffense}",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "Temporal Peak: ${cluster.temporalPeak}",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "Surveillance: ${cluster.activeSurveillance}",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // 8. Verified Mysuru Police & Hospital Directory
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Nearby Emergency Stations (Mysuru City Police & Hospitals)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            emergencyPlaces.forEach { place ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (place.type == PlaceType.POLICE_STATION)
                                            CyanPrimaryLight.copy(alpha = 0.2f)
                                        else
                                            CoralEmergency.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (place.type == PlaceType.POLICE_STATION)
                                        Icons.Default.LocalPolice
                                    else
                                        Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = if (place.type == PlaceType.POLICE_STATION) CyanPrimaryLight else CoralEmergency,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = place.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = place.address,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { LocationHelper.launchEmergencyDial(context, place.phoneNumber) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimaryLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = RakshaNavy900,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    focusedMapLocation = Pair(place.latitude, place.longitude)
                                    viewMode = MapViewMode.REAL_LIFE_STREETS
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimaryLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "View on In-App Google Map",
                                    tint = RakshaNavy900,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 9. Factual Disclaimer Notice
        Surface(
            color = RakshaNavy800,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CyanPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Historical crime density indicates documented police report concentrations (2021–2024). It does not guarantee future individual safety. Exercise standard situational awareness across all zones.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )
            }
        }
    }
}
