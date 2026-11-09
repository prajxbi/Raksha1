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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FraudRiskSeverity
import com.example.data.model.FraudScanResult
import com.example.data.model.FraudScanType
import com.example.data.repository.FraudDetectionEngine
import com.example.ui.components.FraudRiskBadge
import com.example.ui.theme.AmberCaution
import com.example.ui.theme.CoralEmergency
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.viewmodel.RakshaUiState

@Composable
fun DigitalFraudScreen(
    uiState: RakshaUiState,
    sampleInputs: List<FraudDetectionEngine.SampleScam>,
    onScan: (FraudScanType, String) -> Unit,
    onClearScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(FraudScanType.SMS) }
    var inputText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val filteredSamples = sampleInputs.filter { it.type == selectedType }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "Digital Fraud Shield",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Classify SMS phishing, evaluate suspicious URLs, & lookup caller risk",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }

        // TABS: SMS, URL, CALLER
        TabRow(
            selectedTabIndex = selectedType.ordinal,
            containerColor = RakshaNavy800,
            contentColor = CyanPrimaryLight,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedType.ordinal]),
                    color = CyanPrimaryLight
                )
            },
            modifier = Modifier.border(1.dp, SurfaceBorderDark, RoundedCornerShape(10.dp))
        ) {
            FraudScanType.values().forEach { type ->
                val label = when (type) {
                    FraudScanType.SMS -> "SMS Phishing"
                    FraudScanType.URL -> "Suspicious URL"
                    FraudScanType.CALLER -> "Caller Lookup"
                }
                val icon = when (type) {
                    FraudScanType.SMS -> Icons.Default.Message
                    FraudScanType.URL -> Icons.Default.Link
                    FraudScanType.CALLER -> Icons.Default.Phone
                }

                Tab(
                    selected = (selectedType == type),
                    onClick = {
                        selectedType = type
                        inputText = ""
                        onClearScan()
                    },
                    text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("fraud_tab_${type.name.lowercase()}")
                )
            }
        }

        // PRIVACY GUARANTEE & ON-DEVICE CONSENT BANNER
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanPrimaryLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .testTag("privacy_consent_card")
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyanPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% On-Device Privacy & Consent Control",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Your private SMS messages and call records never leave this device. RAKSHA inspects texts and caller logs locally using regex pattern rules, heuristic threat scores, and Karnataka telecom scam signatures without unprompted background access.",
                    color = Color(0xFFBAE6FD),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Quick load realistic SMS scam scenario
                            val sampleScam = "URGENT: BESCOM bill overdue. Your electricity connection will be disconnected tonight by 9:30 PM. Call power officer at +91-98450-99999 immediately or install electricity update APK: http://kptcl-update.live/pay.apk"
                            inputText = sampleScam
                            selectedType = FraudScanType.SMS
                            onScan(FraudScanType.SMS, sampleScam)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("quick_import_sms_btn")
                    ) {
                        Text("📥 Import SMS Dump", fontSize = 10.sp, color = CyanPrimaryLight, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = {
                            // Quick load high-risk spoofed caller
                            val sampleCaller = "+88216900123" // International Satellite Wangiri one-ring fraud
                            inputText = sampleCaller
                            selectedType = FraudScanType.CALLER
                            onScan(FraudScanType.CALLER, sampleCaller)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("quick_import_call_btn")
                    ) {
                        Text("📞 Import Call Log", fontSize = 10.sp, color = CyanPrimaryLight, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // INPUT FIELD & SCAN TRIGGER
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val placeholder = when (selectedType) {
                    FraudScanType.SMS -> "Paste suspicious message text here (e.g. 'Bank KYC deactivation threat')..."
                    FraudScanType.URL -> "Paste web address (e.g. 'http://paypa1-security.live')..."
                    FraudScanType.CALLER -> "Enter telephone number (e.g. '+1 (800) 829-1040')..."
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(placeholder, fontSize = 12.sp, color = Color(0xFF64748B)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fraud_input_field"),
                    minLines = if (selectedType == FraudScanType.SMS) 3 else 1,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanPrimaryLight,
                        unfocusedBorderColor = SurfaceBorderDark,
                        focusedContainerColor = Color(0xFF0B132B),
                        unfocusedContainerColor = Color(0xFF0B132B)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onScan(selectedType, inputText) },
                        enabled = inputText.isNotBlank() && !uiState.isAnalyzingFraud,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimaryLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("fraud_scan_button")
                    ) {
                        if (uiState.isAnalyzingFraud) {
                            CircularProgressIndicator(
                                color = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyze Threat", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    if (inputText.isNotBlank() || uiState.fraudScanResult != null) {
                        OutlinedButton(
                            onClick = {
                                inputText = ""
                                onClearScan()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("fraud_clear_button")
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }

        // QUICK TEST SAMPLES LIBRARY (Click to test)
        Text(
            text = "VERIFIED TEST SAMPLES (CLICK TO EVALUATE)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredSamples) { sample ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RakshaNavy800)
                        .border(1.dp, if (sample.isScam) CoralEmergency.copy(alpha = 0.4f) else EmeraldVerified.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable {
                            inputText = sample.content
                            onScan(selectedType, sample.content)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("sample_${sample.label.replace(" ", "_").lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (sample.isScam) CoralEmergency else EmeraldVerified)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = sample.label,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // SCAN RESULTS CARD
        AnimatedVisibility(visible = uiState.fraudScanResult != null) {
            val result = uiState.fraudScanResult
            if (result != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            when (result.severity) {
                                FraudRiskSeverity.HIGH_RISK -> CoralEmergency
                                FraudRiskSeverity.SUSPICIOUS -> AmberCaution
                                FraudRiskSeverity.SAFE -> EmeraldVerified
                            },
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("fraud_result_card")
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
                            Text(
                                text = result.verdictTitle,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            FraudRiskBadge(severity = result.severity)
                        }

                        // Risk Score Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Threat Confidence Score: ${result.riskScore}/100",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = result.explanation,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        // Detected Triggers
                        if (result.detectedTriggers.isNotEmpty()) {
                            Text(
                                text = "Detected Risk Indicators:",
                                color = CyanPrimaryLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                result.detectedTriggers.forEach { trigger ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (result.severity == FraudRiskSeverity.HIGH_RISK) CoralEmergency else AmberCaution,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = trigger, color = Color(0xFFFECACA), fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Recommended Action
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Recommended Action:",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = result.recommendedAction,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // Model & Dataset Provenance
                        Text(
                            text = "Model Provenance: ${result.datasetProvenance}",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
