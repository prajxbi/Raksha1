package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EmergencyContact
import com.example.data.local.RakshaDatabase
import com.example.data.local.SosAlertLog
import com.example.data.location.LocationHelper
import com.example.data.model.CitySelection
import com.example.data.model.CrimeIncident
import com.example.data.model.CrimeRiskCluster
import com.example.data.model.EmergencyPlace
import com.example.data.model.FraudRiskSeverity
import com.example.data.model.FraudScanResult
import com.example.data.model.FraudScanType
import com.example.data.model.HistoricalRiskLevel
import com.example.data.model.RouteOption
import com.example.data.model.SafetyGuide
import com.example.data.model.UserAccount
import com.example.data.repository.AuthRepository
import com.example.data.repository.RakshaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RakshaScreen(val title: String) {
    HOME("Home"),
    CRIME_MAP("Safety Map"),
    SAFER_ROUTES("Routes"),
    DIGITAL_FRAUD("Fraud Shield"),
    EMERGENCY_SOS("SOS Hub"),
    CONTACTS("Emergency Contacts"),
    ALERT_HISTORY("Alert History"),
    SAFETY_ADVISOR("Safety Playbooks"),
    SETTINGS("Settings & Privacy")
}

data class RakshaUiState(
    val currentScreen: RakshaScreen = RakshaScreen.HOME,
    val selectedCity: CitySelection = CitySelection.MYSURU,
    val isDarkMode: Boolean = true,
    val userLat: Double = CitySelection.MYSURU.centerLat,
    val userLng: Double = CitySelection.MYSURU.centerLng,
    val userAddress: String = CitySelection.MYSURU.defaultAddress,
    val locationRiskLevel: HistoricalRiskLevel = HistoricalRiskLevel.MODERATE,
    val locationRiskScore: Int = 46,
    val selectedCluster: CrimeRiskCluster? = null,
    val selectedIncident: CrimeIncident? = null,
    val selectedRoute: RouteOption? = null,
    val isJourneyActive: Boolean = false,
    val journeyElapsedSeconds: Int = 0,
    val fraudScanResult: FraudScanResult? = null,
    val isAnalyzingFraud: Boolean = false,
    val isSosCountdownActive: Boolean = false,
    val sosCountdownRemaining: Int = 5,
    val latestSosDispatchStatus: String? = null,
    val isDemoMode: Boolean = true,
    val isOnboardingComplete: Boolean = true,
    val proximityAlertDismissed: Boolean = false,
    val faqQuery: String = "",
    val faqResponse: String? = null,
    val isFaqLoading: Boolean = false,
    val showAuthDialog: Boolean = false,
    val currentUser: UserAccount? = null,
    val isLoggedIn: Boolean = true,
    val mysuruAreas: List<com.example.data.model.MysuruArea> = emptyList(),
    val selectedMysuruArea: com.example.data.model.MysuruArea? = null,
    val userGpsAccuracy: Float = 10f,
    val isRealGpsActive: Boolean = false,
    val areaSearchQuery: String = ""
)

class RakshaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = RakshaDatabase.getInstance(application)
    private val authRepo = AuthRepository(application)
    val repository = RakshaRepository(
        contactDao = database.contactDao(),
        alertLogDao = database.alertLogDao(),
        authRepo = authRepo
    )

    private val _uiState = MutableStateFlow(RakshaUiState())
    val uiState: StateFlow<RakshaUiState> = _uiState.asStateFlow()

    // Persistent Room Flows
    val contacts: StateFlow<List<EmergencyContact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val primaryContact: StateFlow<EmergencyContact?> = repository.primaryContact
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val alertLogs: StateFlow<List<SosAlertLog>> = repository.allAlertLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var countdownJob: Job? = null
    private var journeyJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSampleContactsIfEmpty()
            // Observe user auth
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user, isLoggedIn = (user != null)) }
            }
        }
        // Initialize with Mysuru areas, routes and risk
        val allAreas = repository.getMysuruAreas()
        _uiState.update { it.copy(mysuruAreas = allAreas) }
        switchCity(CitySelection.MYSURU)
    }

    fun switchCity(city: CitySelection) {
        val (riskLevel, riskScore) = repository.evaluateLocationRisk(city.centerLat, city.centerLng, city.cityName)
        val routes = repository.computeRoutes(
            originLat = city.centerLat,
            originLng = city.centerLng,
            destLat = if (city == CitySelection.MYSURU) 12.3270 else 37.7940,
            destLng = if (city == CitySelection.MYSURU) 76.6260 else -122.3950,
            city = city.cityName
        )

        _uiState.update {
            it.copy(
                selectedCity = city,
                userLat = city.centerLat,
                userLng = city.centerLng,
                userAddress = city.defaultAddress,
                locationRiskLevel = riskLevel,
                locationRiskScore = riskScore,
                selectedRoute = routes.firstOrNull(),
                selectedCluster = null,
                selectedIncident = null,
                selectedMysuruArea = null,
                proximityAlertDismissed = false
            )
        }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setShowAuthDialog(show: Boolean) {
        _uiState.update { it.copy(showAuthDialog = show) }
    }

    // User Authentication
    fun login(email: String, pass: String): Boolean {
        val success = repository.login(email, pass)
        if (success) {
            _uiState.update { it.copy(showAuthDialog = false) }
        }
        return success
    }

    fun register(name: String, email: String, phone: String, role: String, city: String): Boolean {
        val success = repository.register(name, email, phone, role, city)
        if (success) {
            _uiState.update { it.copy(showAuthDialog = false) }
        }
        return success
    }

    fun quickLogin(role: String) {
        repository.quickLogin(role)
        _uiState.update { it.copy(showAuthDialog = false) }
    }

    fun logout() {
        repository.logout()
    }

    fun getCityIncidents(): List<CrimeIncident> =
        repository.getHistoricalIncidents(_uiState.value.selectedCity.cityName)

    fun getCityClusters(): List<CrimeRiskCluster> =
        repository.getCrimeClusters(_uiState.value.selectedCity.cityName)

    fun getCityEmergencyStations(): List<EmergencyPlace> =
        repository.getEmergencyStations(_uiState.value.selectedCity.cityName)

    fun getSafetyGuides(): List<SafetyGuide> = repository.getSafetyGuides()

    fun navigateTo(screen: RakshaScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun refreshLocationAndRisk() {
        viewModelScope.launch {
            val city = _uiState.value.selectedCity
            val gpsResult = LocationHelper.getFreshRealLocation(getApplication())
            val targetLat = if (gpsResult.isRealGps) gpsResult.latitude else city.centerLat
            val targetLng = if (gpsResult.isRealGps) gpsResult.longitude else city.centerLng
            val targetAddress = if (gpsResult.isRealGps && gpsResult.address.isNotEmpty()) {
                gpsResult.address
            } else {
                city.defaultAddress
            }

            val (riskLevel, riskScore) = repository.evaluateLocationRisk(targetLat, targetLng, city.cityName)
            _uiState.update {
                it.copy(
                    userLat = targetLat,
                    userLng = targetLng,
                    userAddress = targetAddress,
                    userGpsAccuracy = gpsResult.accuracyMeters,
                    isRealGpsActive = gpsResult.isRealGps,
                    locationRiskLevel = riskLevel,
                    locationRiskScore = riskScore
                )
            }
        }
    }

    fun selectMysuruArea(area: com.example.data.model.MysuruArea?) {
        if (area == null) {
            _uiState.update { it.copy(selectedMysuruArea = null) }
            return
        }
        val (riskLevel, riskScore) = repository.evaluateLocationRisk(area.latitude, area.longitude, "Mysuru")
        _uiState.update {
            it.copy(
                selectedMysuruArea = area,
                selectedCluster = null,
                selectedIncident = null,
                userLat = area.latitude,
                userLng = area.longitude,
                userAddress = "${area.name}, Mysuru, Karnataka",
                locationRiskLevel = riskLevel,
                locationRiskScore = riskScore
            )
        }
    }

    fun setAreaSearchQuery(query: String) {
        _uiState.update { it.copy(areaSearchQuery = query) }
    }

    fun selectCluster(cluster: CrimeRiskCluster?) {
        _uiState.update { it.copy(selectedCluster = cluster, selectedIncident = null, selectedMysuruArea = null) }
    }

    fun selectIncident(incident: CrimeIncident?) {
        _uiState.update { it.copy(selectedIncident = incident, selectedCluster = null) }
    }

    fun selectRoute(route: RouteOption) {
        _uiState.update { it.copy(selectedRoute = route) }
    }

    fun startJourneyEscort() {
        if (_uiState.value.isJourneyActive) return
        _uiState.update { it.copy(isJourneyActive = true, journeyElapsedSeconds = 0) }
        journeyJob?.cancel()
        journeyJob = viewModelScope.launch {
            while (_uiState.value.isJourneyActive) {
                delay(1000)
                _uiState.update { it.copy(journeyElapsedSeconds = it.journeyElapsedSeconds + 1) }
            }
        }
    }

    fun stopJourneyEscort() {
        journeyJob?.cancel()
        _uiState.update { it.copy(isJourneyActive = false, journeyElapsedSeconds = 0) }
    }

    fun scanFraudInput(type: FraudScanType, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzingFraud = true) }
            delay(300)
            val result = when (type) {
                FraudScanType.SMS -> repository.analyzeSms(text)
                FraudScanType.URL -> repository.analyzeUrl(text)
                FraudScanType.CALLER -> repository.analyzeCaller(text)
            }
            _uiState.update { it.copy(fraudScanResult = result, isAnalyzingFraud = false) }
        }
    }

    fun clearFraudScan() {
        _uiState.update { it.copy(fraudScanResult = null) }
    }

    fun triggerSos() {
        if (_uiState.value.isSosCountdownActive) return
        _uiState.update {
            it.copy(
                isSosCountdownActive = true,
                sosCountdownRemaining = 5,
                latestSosDispatchStatus = null
            )
        }
        LocationHelper.triggerVibrationPulse(getApplication(), 300L)

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                _uiState.update { it.copy(sosCountdownRemaining = i) }
                LocationHelper.triggerVibrationPulse(getApplication(), 200L)
                delay(1000)
            }
            executeSosDispatch()
        }
    }

    fun cancelSosCountdown() {
        countdownJob?.cancel()
        _uiState.update {
            it.copy(
                isSosCountdownActive = false,
                sosCountdownRemaining = 5,
                latestSosDispatchStatus = "Alert Cancelled by User (False Alarm Recorded)"
            )
        }
        viewModelScope.launch {
            repository.logAlert(
                type = "EMERGENCY_SOS",
                lat = _uiState.value.userLat,
                lng = _uiState.value.userLng,
                address = _uiState.value.userAddress,
                status = "CANCELLED_BY_USER",
                recipientsCount = contacts.value.size,
                notes = "User tapped Cancel within the 5-second countdown window."
            )
        }
    }

    private fun executeSosDispatch() {
        _uiState.update {
            it.copy(
                isSosCountdownActive = false,
                latestSosDispatchStatus = "Emergency SOS Dispatched to Contacts & Prepared for Dialer (${_uiState.value.selectedCity.emergencyNumber})"
            )
        }
        val currentContacts = contacts.value
        val recipientPhones = currentContacts.filter { it.sendSmsAlert }.map { it.phone }
        val mapsLink = "https://maps.google.com/?q=${_uiState.value.userLat},${_uiState.value.userLng}"
        val sosMessage = "EMERGENCY ALERT: I need urgent help! My estimated location: ${_uiState.value.userAddress} ($mapsLink). Sent via RAKSHA Personal Safety."

        if (recipientPhones.isNotEmpty()) {
            LocationHelper.launchEmergencySms(getApplication(), recipientPhones, sosMessage)
        }

        viewModelScope.launch {
            repository.logAlert(
                type = "EMERGENCY_SOS",
                lat = _uiState.value.userLat,
                lng = _uiState.value.userLng,
                address = _uiState.value.userAddress,
                status = "DISPATCHED",
                recipientsCount = recipientPhones.size,
                notes = "Automated SOS text prepared with GPS coordinates and dispatched for ${_uiState.value.selectedCity.cityName}."
            )
        }
    }

    fun triggerTestAlert() {
        viewModelScope.launch {
            val contactCount = contacts.value.size
            repository.logAlert(
                type = "TEST_ALERT",
                lat = _uiState.value.userLat,
                lng = _uiState.value.userLng,
                address = _uiState.value.userAddress,
                status = "TEST_COMPLETED",
                recipientsCount = contactCount,
                notes = "User verified SOS signal pipeline in Test Simulation mode. No emergency sirens dispatched."
            )
            _uiState.update {
                it.copy(latestSosDispatchStatus = "Test SOS simulated successfully in ${_uiState.value.selectedCity.cityName}. Pipeline verified.")
            }
        }
    }

    fun dismissProximityAlert() {
        _uiState.update { it.copy(proximityAlertDismissed = true) }
    }

    fun toggleDemoMode(enabled: Boolean) {
        _uiState.update { it.copy(isDemoMode = enabled) }
    }

    fun setOnboardingComplete(completed: Boolean) {
        _uiState.update { it.copy(isOnboardingComplete = completed) }
    }

    fun addContact(name: String, phone: String, relationship: String, isPrimary: Boolean, sendSms: Boolean) {
        viewModelScope.launch {
            repository.addContact(name, phone, relationship, isPrimary, sendSms)
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun clearAlertLogs() {
        viewModelScope.launch {
            repository.clearAlertHistory()
        }
    }

    fun askFaq(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(faqQuery = query, isFaqLoading = true) }
            delay(350)
            val normalized = query.lowercase()
            val answer = when {
                normalized.contains("mysuru") || normalized.contains("mysore") || normalized.contains("karnataka") ->
                    "Mysuru Safety Guide: For any emergency in Mysuru or across Karnataka, dial 112 (Namma 112). For Devaraja Market / Suburban Bus Stand transit, prefer well-lit Sayyaji Rao Road. Avoid isolated Chamundi Hill hairpins after 8:30 PM. K.R. Hospital provides 24/7 emergency trauma care on Irwin Road."
                normalized.contains("night") || normalized.contains("dark") || normalized.contains("walk") ->
                    "When walking alone after hours: 1) Stick to illuminated major avenues, 2) Keep one ear open (avoid full noise isolation), 3) Pre-share your destination route in RAKSHA, and 4) If you suspect someone is tailing you, immediately head into an active commercial venue."
                normalized.contains("scam") || normalized.contains("call") || normalized.contains("fraud") || normalized.contains("money") ->
                    "Red flags for digital extortion: Legitimate public utilities (like BESCOM/CESC) and banks NEVER demand immediate payment in gift cards, nor do they request your SMS OTP. Call Karnataka Cyber Police at 1930."
                normalized.contains("car") || normalized.contains("uber") || normalized.contains("ride") || normalized.contains("taxi") || normalized.contains("auto") ->
                    "Rideshare & Auto safety: Always check vehicle number plate. In Mysuru, use prepaid auto counters at Railway Station and Suburban Bus Stand. Sit in the rear seat with dual exit options."
                normalized.contains("sos") || normalized.contains("police") || normalized.contains("emergency") ->
                    "In critical life-safety danger, dial 112 directly. RAKSHA provides a 5-second countdown to prevent false alarms, followed by instant SMS coordinate transmission to your trusted emergency circle."
                else ->
                    "Key Safety Rule: Preparation prevents panic. Configure trusted emergency contacts in RAKSHA, check historical route safety before late trips, and inspect suspicious links before entering credentials."
            }
            _uiState.update { it.copy(faqResponse = answer, isFaqLoading = false) }
        }
    }
}
