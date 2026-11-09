package com.example.data.model

/**
 * Representation of a prominent neighborhood/area in Mysuru, Karnataka, India.
 * Includes geographic coordinates, safety scoring, police station jurisdictions,
 * and infrastructure metrics (lighting, CCTV).
 */
data class MysuruArea(
    val id: String,
    val name: String,
    val kannadaName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val zoomLevel: Int = 15,
    val safetyScore: Int, // 0 to 100 (Higher is safer)
    val riskLevel: HistoricalRiskLevel,
    val nearestPoliceStation: String,
    val policePhone: String,
    val streetLightingRating: String,
    val cctvCoverageRating: String,
    val category: String // "Heritage / Tourist", "Residential", "Commercial Hub", "Transit Hub", "Educational", "Industrial"
)
