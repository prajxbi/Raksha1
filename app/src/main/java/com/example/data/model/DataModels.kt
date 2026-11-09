package com.example.data.model

enum class CitySelection(
    val cityName: String,
    val stateCountry: String,
    val centerLat: Double,
    val centerLng: Double,
    val defaultAddress: String,
    val emergencyNumber: String,
    val ambulanceNumber: String
) {
    MYSURU(
        "Mysuru",
        "Karnataka, India",
        12.3052,
        76.6552,
        "Near Mysuru Palace, K.R. Circle, Mysuru",
        "112",
        "108"
    ),
    SAN_FRANCISCO(
        "San Francisco",
        "California, USA",
        37.7833,
        -122.4167,
        "Market St & 5th St, San Francisco, CA",
        "911",
        "911"
    )
}

data class UserAccount(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val userRole: String = "Citizen", // Citizen, Student Commuter, Emergency Volunteer
    val emergencyNote: String = "No known medical allergies. Notify primary contacts immediately.",
    val homeCity: String = "Mysuru",
    val isVerified: Boolean = true
)

enum class HistoricalRiskLevel(val label: String, val disclaimer: String) {
    LOW(
        "Low Historical Concentration",
        "Documented crime reports are historically sparse in this zone. Never guarantees individual safety."
    ),
    MODERATE(
        "Moderate Incident Density",
        "Intermittent property or transit-related reports historically recorded. Maintain standard situational awareness."
    ),
    ELEVATED(
        "Elevated Historical Concentration",
        "Higher historical frequency of police incident reports documented (2021–2024). Exercise heightened awareness."
    )
}

data class CrimeIncident(
    val id: String,
    val incidentNumber: String,
    val category: String,
    val offenseDescription: String,
    val date: String,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val policeDistrict: String,
    val weaponInvolved: Boolean,
    val historicalSeverityScore: Double,
    val city: String = "Mysuru"
)

data class CrimeRiskCluster(
    val clusterId: String,
    val title: String,
    val centerLat: Double,
    val centerLng: Double,
    val radiusMeters: Double,
    val historicalIncidentCount: Int,
    val riskLevel: HistoricalRiskLevel,
    val predominantOffense: String,
    val temporalPeak: String,
    val activeSurveillance: String,
    val safetyAwarenessTips: List<String>,
    val city: String = "Mysuru"
)

enum class RouteType {
    FASTEST,
    SAFER_RECOMMENDED,
    TRANSIT_CORRIDOR
}

data class RoutePoint(
    val lat: Double,
    val lng: Double,
    val label: String? = null
)

data class RouteOption(
    val routeId: String,
    val name: String,
    val type: RouteType,
    val distanceKm: Double,
    val etaMinutes: Int,
    val historicalRiskLevel: HistoricalRiskLevel,
    val exposureScore: Int, // 0 - 100
    val path: List<RoutePoint>,
    val safetyAdvantages: List<String>,
    val tradeoffs: String,
    val infrastructureSummary: String,
    val city: String = "Mysuru"
)

enum class FraudScanType {
    SMS,
    URL,
    CALLER
}

enum class FraudRiskSeverity {
    SAFE,
    SUSPICIOUS,
    HIGH_RISK
}

data class FraudScanResult(
    val type: FraudScanType,
    val targetInput: String,
    val severity: FraudRiskSeverity,
    val riskScore: Int, // 0 - 100
    val verdictTitle: String,
    val explanation: String,
    val detectedTriggers: List<String>,
    val indicators: List<Pair<String, String>>,
    val recommendedAction: String,
    val datasetProvenance: String
)

enum class PlaceType {
    POLICE_STATION,
    HOSPITAL,
    EMERGENCY_DISPATCH
}

data class EmergencyPlace(
    val id: String,
    val name: String,
    val type: PlaceType,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phoneNumber: String,
    val distanceMeters: Int,
    val isOpen24Hours: Boolean,
    val city: String = "Mysuru"
)

data class SafetyGuide(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val checklist: List<String>,
    val officialHelpline: String
)
