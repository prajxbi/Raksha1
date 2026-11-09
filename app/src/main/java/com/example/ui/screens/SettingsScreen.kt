package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.AmberCaution
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark

@Composable
fun SettingsScreen(
    isDemoMode: Boolean,
    onToggleDemoMode: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val hasLocation = LocationHelper.hasLocationPermission(context)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(RakshaNavy800)
                    .border(1.dp, SurfaceBorderDark, CircleShape)
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Settings & Privacy Controls",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Data handling, demo mode, and system permissions",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // 1. DEMO MODE TOGGLE
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Realistic Demo Mode",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(AmberCaution.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isDemoMode) "ACTIVE" else "OFF",
                                color = AmberCaution,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Loads documented open police incident records (SFPD & Chicago schema) and preset routes for evaluation. Clearly labelled across all screens.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Switch(
                    checked = isDemoMode,
                    onCheckedChange = { onToggleDemoMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0F172A),
                        checkedTrackColor = CyanPrimaryLight
                    ),
                    modifier = Modifier.testTag("demo_mode_switch")
                )
            }
        }

        // 2. PRIVACY & LOCAL PROCESSING
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldVerified, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zero-Cloud Local Privacy Guarantee",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "• All emergency contacts and SOS alert logs are stored exclusively in your device's encrypted Room SQLite database.\n" +
                            "• SMS messages and phone numbers submitted to Digital Fraud Shield are analyzed locally; they are never uploaded to remote marketing databases.\n" +
                            "• RAKSHA contains zero third-party advertising SDKs or tracking cookies.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // 3. SYSTEM PERMISSIONS STATUS
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "SYSTEM PERMISSIONS STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                PermissionStatusRow(
                    name = "Precise Location (GPS)",
                    description = "Required for crime risk proximity & SOS coordinates",
                    isGranted = hasLocation
                )

                PermissionStatusRow(
                    name = "Vibration & Haptics",
                    description = "Required for audible/haptic SOS countdown pulse",
                    isGranted = true
                )

                PermissionStatusRow(
                    name = "Emergency Calling & SMS",
                    description = "Standard system dialer & SMS intents (zero-permission, safe)",
                    isGranted = true
                )
            }
        }

        // 4. DATASET PROVENANCE & TECHNICAL SPECS
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CyanPrimaryLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dataset & Model Provenance",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "1. Historical Crime Dataset: Derived from verified public municipal police records (SFPD / City Open Data standards, 2021–2024).\n" +
                            "2. Geographic Scoring: Kernel Density Estimation (KDE) with Gaussian distance decay and category severity weighting.\n" +
                            "3. SMS Scam Model: Trained and calibrated against the UCI Machine Learning SMS Spam Corpus, CERT-In mobile threats, and FTC phishing catalogs.\n" +
                            "4. Routing Engine: Evaluates trade-offs between shortest path distance and historical incident concentration along street corridors.\n" +
                            "5. Disclaimer: Historical report density does not guarantee or predict future safety.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(
    name: String,
    description: String,
    isGranted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = description, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
        Box(
            modifier = Modifier
                .background(
                    if (isGranted) EmeraldVerified.copy(alpha = 0.2f) else AmberCaution.copy(alpha = 0.2f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isGranted) "GRANTED" else "DEFAULT FALLBACK",
                color = if (isGranted) EmeraldVerified else AmberCaution,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
