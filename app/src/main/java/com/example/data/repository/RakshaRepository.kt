package com.example.data.repository

import android.content.Context
import com.example.data.local.AlertLogDao
import com.example.data.local.ContactDao
import com.example.data.local.EmergencyContact
import com.example.data.local.SosAlertLog
import com.example.data.model.CitySelection
import com.example.data.model.CrimeIncident
import com.example.data.model.CrimeRiskCluster
import com.example.data.model.EmergencyPlace
import com.example.data.model.FraudScanResult
import com.example.data.model.HistoricalRiskLevel
import com.example.data.model.MysuruArea
import com.example.data.model.RouteOption
import com.example.data.model.SafetyGuide
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class RakshaRepository(
    private val contactDao: ContactDao,
    private val alertLogDao: AlertLogDao,
    private val authRepo: AuthRepository,
    private val crimeDataRepo: CrimeDataRepository = CrimeDataRepository(),
    private val routingRepo: RoutingRepository = RoutingRepository(),
    private val fraudEngine: FraudDetectionEngine = FraudDetectionEngine()
) {
    // Auth & User Profile
    val currentUser: StateFlow<UserAccount?> = authRepo.currentUser
    val isLoggedIn: StateFlow<Boolean> = authRepo.isLoggedIn

    fun login(email: String, pass: String): Boolean = authRepo.login(email, pass)
    fun register(name: String, email: String, phone: String, role: String, city: String): Boolean =
        authRepo.register(name, email, phone, role, city)
    fun quickLogin(role: String) = authRepo.quickLogin(role)
    fun logout() = authRepo.logout()

    // Emergency Contacts
    val allContacts: Flow<List<EmergencyContact>> = contactDao.getAllContacts()
    val primaryContact: Flow<EmergencyContact?> = contactDao.getPrimaryContact()

    suspend fun addContact(name: String, phone: String, relationship: String, isPrimary: Boolean, sendSms: Boolean) {
        if (isPrimary) {
            contactDao.clearPrimaryFlags()
        }
        contactDao.insertContact(
            EmergencyContact(
                name = name,
                phone = phone,
                relationship = relationship,
                isPrimary = isPrimary,
                sendSmsAlert = sendSms
            )
        )
    }

    suspend fun updateContact(contact: EmergencyContact) {
        if (contact.isPrimary) {
            contactDao.clearPrimaryFlags()
        }
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        contactDao.deleteContact(contact)
    }

    suspend fun deleteContactById(id: Int) {
        contactDao.deleteContactById(id)
    }

    suspend fun ensureSampleContactsIfEmpty() {
        if (contactDao.getCount() == 0) {
            addContact(
                name = "Suresh Kumar",
                phone = "+91 98450 11223",
                relationship = "Brother (Mysuru)",
                isPrimary = true,
                sendSms = true
            )
            addContact(
                name = "Karnataka Emergency (Namma 112)",
                phone = "112",
                relationship = "Police Emergency",
                isPrimary = false,
                sendSms = false
            )
            addContact(
                name = "K.R. Hospital Emergency Trauma",
                phone = "0821-2520512",
                relationship = "Trauma Care",
                isPrimary = false,
                sendSms = false
            )
        }
    }

    // Alert Logs
    val allAlertLogs: Flow<List<SosAlertLog>> = alertLogDao.getAllLogs()

    suspend fun logAlert(
        type: String,
        lat: Double,
        lng: Double,
        address: String,
        status: String,
        recipientsCount: Int,
        notes: String
    ): Long {
        return alertLogDao.insertLog(
            SosAlertLog(
                alertType = type,
                latitude = lat,
                longitude = lng,
                address = address,
                status = status,
                recipientsCount = recipientsCount,
                notes = notes
            )
        )
    }

    suspend fun clearAlertHistory() {
        alertLogDao.clearAll()
    }

    // Crime Data (City specific)
    fun getHistoricalIncidents(city: String = "Mysuru"): List<CrimeIncident> =
        crimeDataRepo.getHistoricalIncidents(city)

    fun getCrimeClusters(city: String = "Mysuru"): List<CrimeRiskCluster> =
        crimeDataRepo.getCrimeClusters(city)

    fun getEmergencyStations(city: String = "Mysuru"): List<EmergencyPlace> =
        crimeDataRepo.getEmergencyStations(city)

    fun getMysuruAreas(): List<MysuruArea> =
        crimeDataRepo.getMysuruAreas()

    fun findMysuruAreas(query: String): List<MysuruArea> =
        crimeDataRepo.findAreaByName(query)

    fun evaluateLocationRisk(lat: Double, lng: Double, city: String = "Mysuru"): Pair<HistoricalRiskLevel, Int> =
        crimeDataRepo.evaluateLocationRisk(lat, lng, city)

    // Routing
    fun getDestinationPresets(city: String = "Mysuru"): List<RoutingRepository.DestinationPreset> =
        routingRepo.getPresets(city)

    fun computeRoutes(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double,
        city: String = "Mysuru"
    ): List<RouteOption> = routingRepo.computeRouteOptions(originLat, originLng, destLat, destLng, city)

    // Fraud Shield
    val fraudSampleInputs = fraudEngine.sampleInputs
    fun analyzeSms(message: String): FraudScanResult = fraudEngine.analyzeSms(message)
    fun analyzeUrl(url: String): FraudScanResult = fraudEngine.analyzeUrl(url)
    fun analyzeCaller(phone: String): FraudScanResult = fraudEngine.analyzeCaller(phone)

    // Safety Guides
    fun getSafetyGuides(): List<SafetyGuide> = listOf(
        SafetyGuide(
            id = "MYS-GUIDE-TRANSIT",
            title = "Mysuru Evening & Transit Protocol",
            category = "Mysuru Transit Safety",
            summary = "Precautions when commuting between KSRTC Suburban Bus Stand, Railway Station, and Chamundi Foothills.",
            checklist = listOf(
                "Use official prepaid auto counter booths at Suburban Bus Stand and Railway Station.",
                "Karnataka Namma 112 is active 24/7 with GPS response vehicles stationed across Mysuru city.",
                "Prefer well-lit Sayyaji Rao Road, JLB Road, and D. Devaraja Urs Road over rear unlit market lanes.",
                "Avoid stopping vehicles on isolated Chamundi Hill hairpin turns after 8:30 PM.",
                "If feeling uneasy, approach nearest police station (Devaraja, Lashkar, Saraswathipuram) or call 112."
            ),
            officialHelpline = "Karnataka Unified Emergency: 112 | Mysuru Police: 0821-2418306 | Medical Ambulance: 108"
        ),
        SafetyGuide(
            id = "GUIDE-NIGHT-TRAVEL",
            title = "Solo Night Travel Protocol",
            category = "Physical Safety",
            summary = "Proactive precautions when commuting after hours in unfamiliar areas.",
            checklist = listOf(
                "Keep one ear free: Avoid noise-cancelling headphones in low-visibility streets.",
                "Share live location link with your primary emergency contact before departure.",
                "Stay along illuminated commercial boulevards with regular storefront activity.",
                "Keep phone and car keys accessible in your coat pocket, not buried in luggage."
            ),
            officialHelpline = "Emergency: 112 / 911"
        ),
        SafetyGuide(
            id = "GUIDE-DIGITAL-FRAUD",
            title = "Spotting & Defusing Impersonation Scams",
            category = "Cyber & Financial",
            summary = "How to handle aggressive messages claiming power disconnection or bank blocks.",
            checklist = listOf(
                "Hang up immediately on robotic or intimidating calls threatening arrest.",
                "Government agencies, KPTCL/BESCOM, and banks NEVER demand OTP or payment via gift cards.",
                "Never tap on links ending in .apk or non-standard web extensions (.xyz, .top).",
                "Report scam SMS messages to official spam registries (1930 / cybercrime.gov.in)."
            ),
            officialHelpline = "National Cyber Fraud Helpline: 1930"
        )
    )
}
