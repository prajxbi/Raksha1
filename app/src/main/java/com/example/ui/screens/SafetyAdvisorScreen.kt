package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.SafetyGuide
import com.example.ui.theme.CyanPrimaryLight
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.RakshaNavy800
import com.example.ui.theme.RakshaNavy900
import com.example.ui.theme.SurfaceBorderDark

@Composable
fun SafetyAdvisorScreen(
    guides: List<SafetyGuide>,
    faqQuery: String,
    faqResponse: String?,
    isFaqLoading: Boolean,
    onAskFaq: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var selectedGuide by remember { mutableStateOf<SafetyGuide?>(null) }
    var currentQuery by remember { mutableStateOf(faqQuery) }
    val scrollState = rememberScrollState()

    val suggestedQuestions = listOf(
        "How do I walk safely at night?",
        "What should I do if a scammer calls?",
        "How to verify an Uber / rideshare?",
        "What happens when SOS is triggered?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RakshaNavy900)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                    .testTag("advisor_back_button")
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
                    text = "Safety Playbooks & Advisor",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "De-escalation protocols and instant safety guidance",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // INTERACTIVE SAFETY ADVISOR Q&A
        Card(
            colors = CardDefaults.cardColors(containerColor = RakshaNavy800),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = CyanPrimaryLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Safety Advisor",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Ask any question regarding physical personal safety, digital fraud, or transit protocols:",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = currentQuery,
                        onValueChange = { currentQuery = it },
                        placeholder = { Text("Ask a safety question...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f).testTag("faq_query_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanPrimaryLight,
                            unfocusedBorderColor = SurfaceBorderDark,
                            focusedContainerColor = Color(0xFF0B132B),
                            unfocusedContainerColor = Color(0xFF0B132B)
                        )
                    )

                    Button(
                        onClick = { onAskFaq(currentQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimaryLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp).testTag("ask_faq_button")
                    ) {
                        if (isFaqLoading) {
                            CircularProgressIndicator(color = Color(0xFF0F172A), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Suggested chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(suggestedQuestions) { q ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(6.dp))
                                .clickable {
                                    currentQuery = q
                                    onAskFaq(q)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = q, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                        }
                    }
                }

                // Response Card
                AnimatedVisibility(visible = faqResponse != null) {
                    if (faqResponse != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF082F49), RoundedCornerShape(10.dp))
                                .border(1.dp, CyanPrimaryLight.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                                .testTag("faq_response_card")
                        ) {
                            Column {
                                Text(
                                    text = "Advisor Recommendation:",
                                    color = CyanPrimaryLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = faqResponse,
                                    color = Color(0xFFF0F9FF),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // CURATED SAFETY PLAYBOOKS
        Text(
            text = "VERIFIED SAFETY PLAYBOOKS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        guides.forEach { guide ->
            val isExpanded = (selectedGuide?.id == guide.id)
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isExpanded) Color(0xFF1E2E4A) else RakshaNavy800),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isExpanded) CyanPrimaryLight else SurfaceBorderDark, RoundedCornerShape(14.dp))
                    .clickable { selectedGuide = if (isExpanded) null else guide }
                    .testTag("guide_card_${guide.id.lowercase()}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = guide.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = guide.category,
                                color = CyanPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isExpanded) CyanPrimaryLight else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = guide.summary,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )

                    // Expanded Checklist
                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Action Checklist:",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            guide.checklist.forEach { item ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldVerified,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Helpline: ${guide.officialHelpline}",
                                    color = Color(0xFFBAE6FD),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
