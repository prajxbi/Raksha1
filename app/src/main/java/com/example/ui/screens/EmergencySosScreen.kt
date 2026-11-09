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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.EmergencyContact
import com.example.data.location.LocationHelper
import com.example.data.model.EmergencyPlace
import com.example.data.model.PlaceType
import com.example.ui.theme.AmberCaution
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralEmergency
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.viewmodel.RakshaScreen
import com.example.ui.viewmodel.RakshaUiState

@Composable
fun EmergencySosScreen(
    uiState: RakshaUiState,
    contacts: List<EmergencyContact>,
    emergencyPlaces: List<EmergencyPlace>,
    onTriggerSos: () -> Unit,
    onCancelSos: () -> Unit,
    onTriggerTestAlert: () -> Unit,
    onAddContact: (String, String, String, Boolean, Boolean) -> Unit,
    onDeleteContact: (EmergencyContact) -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showAddDialog by remember { mutableStateOf(false) }

    val mapsLink = "https://maps.google.com/?q=${uiState.userLat},${uiState.userLng}"
    val messagePreview = "EMERGENCY ALERT: I need urgent help! My estimated location is: ${uiState.userAddress} ($mapsLink). Sent via RAKSHA Safety App."

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Emergency SOS Hub",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configurable contacts, GPS dispatch & emergency shortcuts",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(RakshaNavy800)
                    .border(1.dp, SurfaceBorderDark, CircleShape)
                    .testTag("open_alert_history_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Alert History Log",
                    tint = CyanPrimaryLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // COUNTDOWN BANNER (if active)
        AnimatedVisibility(visible = uiState.isSosCountdownActive) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CoralContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CoralEmergency, RoundedCornerShape(16.dp))
                    .testTag("sos_active_countdown_box")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EMERGENCY SOS IN PROGRESS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Dispatching alert in ${uiState.sosCountdownRemaining} seconds...",
                        color = Color(0xFFFECACA),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onCancelSos,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = CoralEmergency),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("sos_screen_cancel_btn")
                    ) {
                        Text("CANCEL (FALSE ALARM)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // BIG TRIGGER BUTTON
        Card(
            colors = CardDefaults.cardColors(containerColor = CoralContainer.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CoralEmergency, RoundedCornerShape(20.dp))
                .clickable { onTriggerSos() }
                .testTag("sos_big_trigger_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CoralEmergency),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "SOS Button",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PRESS FOR EMERGENCY SOS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Triggers 5-second cancel buffer, vibrates, and prepares GPS text to your ${contacts.size} emergency contacts",
                    color = Color(0xFFFECACA),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // TEST ALERT & STATUS CONTROLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onTriggerTestAlert,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimaryLight),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(10.dp))
                    .testTag("test_sos_pipeline_btn")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Pipeline", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { LocationHelper.launchEmergencyDial(context, "911") },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralEmergency),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, CoralEmergency.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .testTag("sos_dial_911_btn")
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Dial 911 Direct", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // EMERGENCY MESSAGE PREVIEW
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EMERGENCY SMS PREVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    IconButton(
                        onClick = {
                            val phones = contacts.filter { it.sendSmsAlert }.map { it.phone }
                            LocationHelper.launchEmergencySms(context, phones, messagePreview)
                        },
                        modifier = Modifier.size(28.dp).testTag("preview_send_sms_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send Preview", tint = CyanPrimaryLight, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = messagePreview,
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // CONFIGURE EMERGENCY CONTACTS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EMERGENCY CONTACTS (${contacts.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimaryLight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp).testTag("add_contact_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Contact", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RakshaNavy800, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No emergency contacts registered. Tap '+ Add Contact' to set up trusted family or friends.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            contacts.forEach { contact ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (contact.isPrimary) CyanPrimaryLight else SurfaceBorderDark, RoundedCornerShape(12.dp))
                        .testTag("contact_item_${contact.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (contact.isPrimary) CyanPrimaryLight.copy(alpha = 0.2f) else Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (contact.isPrimary) Icons.Default.Star else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (contact.isPrimary) CyanPrimaryLight else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = contact.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (contact.isPrimary) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PRIMARY",
                                        color = CyanPrimaryLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "${contact.relationship} • ${contact.phone}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { LocationHelper.launchEmergencyDial(context, contact.phone) },
                            modifier = Modifier.size(36.dp).testTag("call_contact_${contact.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = EmeraldVerified, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { onDeleteContact(contact) },
                            modifier = Modifier.size(36.dp).testTag("delete_contact_${contact.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // NEARBY RESCUE & POLICE STATIONS
        Text(
            text = "VERIFIED EMERGENCY STATIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        emergencyPlaces.forEach { place ->
            Card(
                colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (place.type == PlaceType.POLICE_STATION) Color(0xFF0284C7).copy(alpha = 0.2f)
                                else Color(0xFFE11D48).copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (place.type == PlaceType.POLICE_STATION) Icons.Default.LocalPolice else Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = if (place.type == PlaceType.POLICE_STATION) Color(0xFF38BDF8) else Color(0xFFF43F5E),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = place.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${place.distanceMeters}m • ${place.address}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = { LocationHelper.launchEmergencyDial(context, place.phoneNumber) },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = CyanPrimaryLight, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // ADD CONTACT DIALOG
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var relationship by remember { mutableStateOf("Family") }
        var isPrimary by remember { mutableStateOf(false) }
        var sendSms by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Emergency Contact", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_contact_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("add_contact_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        label = { Text("Relationship (e.g. Sister, Partner)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_contact_rel_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isPrimary,
                            onCheckedChange = { isPrimary = it },
                            colors = CheckboxDefaults.colors(checkedColor = CyanPrimaryLight)
                        )
                        Text("Set as Primary Contact", color = Color.White, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            onAddContact(name, phone, relationship, isPrimary, sendSms)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimaryLight),
                    modifier = Modifier.testTag("save_contact_btn")
                ) {
                    Text("Save Contact", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = RakshaNavy800
        )
    }
}
