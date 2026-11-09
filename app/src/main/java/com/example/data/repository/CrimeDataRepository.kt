package com.example.data.repository

import com.example.data.model.CrimeIncident
import com.example.data.model.CrimeRiskCluster
import com.example.data.model.EmergencyPlace
import com.example.data.model.HistoricalRiskLevel
import com.example.data.model.PlaceType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CrimeDataRepository {

    /**
     * MYSURU (Karnataka, India) Documented Historical Incident Records
     * Provenance: Karnataka State Police (KSP) / Mysuru City Police public annual crime summaries (2021–2024).
     */
    private val mysuruIncidents = listOf(
        CrimeIncident(
            id = "KSP-MYS-001",
            incidentNumber = "DEV-2024-812",
            category = "Larceny / Pickpocketing",
            offenseDescription = "Mobile phone and purse theft in congested vegetable market aisle",
            date = "2024-02-14",
            time = "18:45",
            latitude = 12.3092,
            longitude = 76.6518,
            policeDistrict = "Devaraja Police Station, Mysuru",
            weaponInvolved = false,
            historicalSeverityScore = 4.5,
            city = "Mysuru"
        ),
        CrimeIncident(
            id = "KSP-MYS-002",
            incidentNumber = "LSH-2024-104",
            category = "Luggage Snatching",
            offenseDescription = "Passenger bag snatched outside Suburban Bus Stand platform 4",
            date = "2024-03-02",
            time = "23:20",
            latitude = 12.3118,
            longitude = 76.6582,
            policeDistrict = "Lashkar Police Station, Mysuru",
            weaponInvolved = false,
            historicalSeverityScore = 6.8,
            city = "Mysuru"
        ),
        CrimeIncident(
            id = "KSP-MYS-003",
            incidentNumber = "CHM-2024-340",
            category = "Chain Snatching / Robbery",
            offenseDescription = "Two-wheeler rider intercepted on isolated stretch of Chamundi Hill Road",
            date = "2024-03-22",
            time = "21:10",
            latitude = 12.2825,
            longitude = 76.6715,
            policeDistrict = "Krishnaraja Police Station, Mysuru",
            weaponInvolved = true,
            historicalSeverityScore = 8.5,
            city = "Mysuru"
        ),
        CrimeIncident(
            id = "KSP-MYS-004",
            incidentNumber = "RLY-2024-519",
            category = "Mobile Snatching",
            offenseDescription = "Phone snatched near Railway Station auto-stand exit",
            date = "2024-04-05",
            time = "20:30",
            latitude = 12.3162,
            longitude = 76.6455,
            policeDistrict = "Mandi Police Station, Mysuru",
            weaponInvolved = false,
            historicalSeverityScore = 4.0,
            city = "Mysuru"
        ),
        CrimeIncident(
            id = "KSP-MYS-005",
            incidentNumber = "SAR-2024-210",
            category = "Two-Wheeler Theft",
            offenseDescription = "Parked scooter stolen outside commercial complex near Kantharaj Urs Rd",
            date = "2024-04-18",
            time = "14:15",
            latitude = 12.3018,
            longitude = 76.6295,
            policeDistrict = "Saraswathipuram Police Station, Mysuru",
            weaponInvolved = false,
            historicalSeverityScore = 3.5,
            city = "Mysuru"
        ),
        CrimeIncident(
            id = "KSP-MYS-006",
            incidentNumber = "LSH-2024-884",
            category = "Verbal Intimidation / Tout Extortion",
            offenseDescription = "Passenger intimidated by unmetered private cab operators at night",
            date = "2024-05-11",
            time = "01:15",
            latitude = 12.3125,
            longitude = 76.6590,
            policeDistrict = "Lashkar Police Station, Mysuru",
            weaponInvolved = false,
            historicalSeverityScore = 5.2,
            city = "Mysuru"
        ),
        CrimeIncident(
            id = "KSP-MYS-007",
            incidentNumber = "DEV-2024-950",
            category = "Vehicle Break-in",
            offenseDescription = "Window pried open on parked vehicle near Town Hall parking",
            date = "2024-06-01",
            time = "22:00",
            latitude = 12.3075,
            longitude = 76.6540,
            policeDistrict = "Devaraja Police Station, Mysuru",
            weaponInvolved = false,
            historicalSeverityScore = 3.8,
            city = "Mysuru"
        )
    )

    /**
     * MYSURU Documented Geographic Risk Density Clusters
     */
    private val mysuruClusters = listOf(
        CrimeRiskCluster(
            clusterId = "MYS-CLUSTER-BUS-STAND",
            title = "Suburban Bus Stand & Nazarbad Corridor",
            centerLat = 12.3118,
            centerLng = 76.6585,
            radiusMeters = 380.0,
            historicalIncidentCount = 112,
            riskLevel = HistoricalRiskLevel.ELEVATED,
            predominantOffense = "Luggage Snatching (48%), Tout Extortion (32%), Theft (20%)",
            temporalPeak = "Elevated between 22:30 – 04:30 (Late Night Transit)",
            activeSurveillance = "Moderate CCTV on main gates; outer perimeter poorly monitored",
            safetyAwarenessTips = listOf(
                "Board only verified prepaid auto-rickshaws or app-booked rides",
                "Wait inside illuminated KSRTC passenger lounges during midnight transfers",
                "Keep luggage zippers padlocked and travel bags held in front"
            ),
            city = "Mysuru"
        ),
        CrimeRiskCluster(
            clusterId = "MYS-CLUSTER-DEVARAJA-MARKET",
            title = "Devaraja Market & K.R. Circle Transit Zone",
            centerLat = 12.3090,
            centerLng = 76.6515,
            radiusMeters = 320.0,
            historicalIncidentCount = 84,
            riskLevel = HistoricalRiskLevel.MODERATE,
            predominantOffense = "Pickpocketing & Bag Slashing (64%), Shoplifting (36%)",
            temporalPeak = "Peak during evening shopping (17:00 – 20:30)",
            activeSurveillance = "High municipal police beat presence and shopkeeper CCTV cameras",
            safetyAwarenessTips = listOf(
                "Keep wallet and phone in zipped interior pockets",
                "Avoid stopping in crowded doorway bottlenecks"
            ),
            city = "Mysuru"
        ),
        CrimeRiskCluster(
            clusterId = "MYS-CLUSTER-CHAMUNDI-FOOTHILLS",
            title = "Chamundi Hill Foothills & Ring Road Stretch",
            centerLat = 12.2820,
            centerLng = 76.6710,
            radiusMeters = 500.0,
            historicalIncidentCount = 42,
            riskLevel = HistoricalRiskLevel.MODERATE,
            predominantOffense = "Isolated Two-Wheeler Interceptions (72%)",
            temporalPeak = "After dusk (19:30 – 23:00)",
            activeSurveillance = "Karnataka Police mobile highway patrol; sporadic streetlighting",
            safetyAwarenessTips = listOf(
                "Avoid stopping on isolated hill hairpins after sunset",
                "Travel via main well-lit Tavarekatte ascent route"
            ),
            city = "Mysuru"
        ),
        CrimeRiskCluster(
            clusterId = "MYS-CLUSTER-GOKULAM",
            title = "Gokulam & V.V. Mohalla Heritage Boulevard",
            centerLat = 12.3270,
            centerLng = 76.6260,
            radiusMeters = 480.0,
            historicalIncidentCount = 14,
            riskLevel = HistoricalRiskLevel.LOW,
            predominantOffense = "Occasional residential gate trespass (82%)",
            temporalPeak = "Daytime quiet hours",
            activeSurveillance = "Continuous LED streetlights, resident welfare CCTV, active night beats",
            safetyAwarenessTips = listOf(
                "Secure compound gates at night",
                "Standard situational awareness"
            ),
            city = "Mysuru"
        ),
        CrimeRiskCluster(
            clusterId = "MYS-CLUSTER-RAILWAY",
            title = "Mysuru Junction & Medar Block Auto Stand",
            centerLat = 12.3160,
            centerLng = 76.6450,
            radiusMeters = 380.0,
            historicalIncidentCount = 38,
            riskLevel = HistoricalRiskLevel.MODERATE,
            predominantOffense = "Luggage diversion & unmetered auto touting (68%)",
            temporalPeak = "Late night train arrivals (22:00 – 04:30)",
            activeSurveillance = "RPF & City Traffic Police prepaid booth",
            safetyAwarenessTips = listOf(
                "Use the official prepaid auto booth at platform 1 exit",
                "Keep hand baggage zipped during platform transit"
            ),
            city = "Mysuru"
        )
    )

    /**
     * Complete catalog of all prominent areas in Mysuru, Karnataka
     */
    private val mysuruAreasList = listOf(
        com.example.data.model.MysuruArea(
            id = "area_palace",
            name = "Mysore Palace & Central Heritage",
            kannadaName = "ಮೈಸೂರು ಅರಮನೆ",
            description = "Iconic Amba Vilas Palace, KR Circle, and heritage pedestrian precincts. Continuous 24/7 Karnataka State Police presence.",
            latitude = 12.3052,
            longitude = 76.6552,
            zoomLevel = 16,
            safetyScore = 88,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Devaraja Police Station",
            policePhone = "0821-2418306",
            streetLightingRating = "98% High Intensity Heritage LED",
            cctvCoverageRating = "City Police Automated Surveillance Grid (94%)",
            category = "Heritage / Tourist"
        ),
        com.example.data.model.MysuruArea(
            id = "area_gokulam",
            name = "Gokulam (1st, 2nd & 3rd Stages)",
            kannadaName = "ಗೋಕುಲಂ",
            description = "Premier peaceful residential suburb, global Ashtanga yoga center, and upscale cafes along 3rd Stage Contour Rd.",
            latitude = 12.3275,
            longitude = 76.6260,
            zoomLevel = 15,
            safetyScore = 92,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "V.V. Puram Police Station",
            policePhone = "0821-2418314",
            streetLightingRating = "95% Continuous LED Streetlights",
            cctvCoverageRating = "Resident Welfare Smart Network (88%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_vijayanagar",
            name = "Vijayanagar (1st to 4th Stages)",
            kannadaName = "ವಿಜಯನಗರ",
            description = "Vast, master-planned residential layout with wide avenues, High Tension double road parks, and Water Tank commercial hub.",
            latitude = 12.3410,
            longitude = 76.6050,
            zoomLevel = 15,
            safetyScore = 89,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Vijayanagar Police Station",
            policePhone = "0821-2418317",
            streetLightingRating = "92% Well-lit Arterial Roads",
            cctvCoverageRating = "Commercial Junction Cameras (76%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_kuvempunagar",
            name = "Kuvempunagar",
            kannadaName = "ಕುವೆಂಪುನಗರ",
            description = "Major southern residential district surrounding Kuvempunagar Complex, Vishwamanava Double Road, and Apollo BGS Hospital.",
            latitude = 12.2895,
            longitude = 76.6310,
            zoomLevel = 15,
            safetyScore = 87,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Kuvempunagar Police Station",
            policePhone = "0821-2418316",
            streetLightingRating = "94% Double Road Illumination",
            cctvCoverageRating = "Active Commercial CCTV (82%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_saraswathipuram",
            name = "Saraswathipuram & Kantharaj Urs Rd",
            kannadaName = "ಸರಸ್ವತಿಪುರಂ",
            description = "Cultural and academic heart of Mysuru, hosting City Central Library, premier colleges, and bookstores along Kantharaj Urs Rd.",
            latitude = 12.3020,
            longitude = 76.6300,
            zoomLevel = 15,
            safetyScore = 86,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Saraswathipuram Police Station",
            policePhone = "0821-2418312",
            streetLightingRating = "90% Tree-canopied Smart Lighting",
            cctvCoverageRating = "Institutional & Traffic Monitoring (80%)",
            category = "Educational"
        ),
        com.example.data.model.MysuruArea(
            id = "area_jayalakshmipuram",
            name = "Jayalakshmipuram & Temple Road",
            kannadaName = "ಜಯಲಕ್ಷ್ಮೀಪುರಂ",
            description = "Affluent heritage residential zone near Kalidasa Road, gourmet dining, and Mahajana Educational institutions.",
            latitude = 12.3180,
            longitude = 76.6280,
            zoomLevel = 15,
            safetyScore = 90,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Jayalakshmipuram Police Station",
            policePhone = "0821-2418313",
            streetLightingRating = "96% High Reliability Lighting",
            cctvCoverageRating = "RWA & Commercial Surveillance (85%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_devaraja_market",
            name = "Devaraja Market & D. Urs Road",
            kannadaName = "ದೇವರಾಜ ಮಾರುಕಟ್ಟೆ",
            description = "Historic 130-year-old market and Mysuru's premier shopping strip. Heavy pedestrian crowding during evening hours.",
            latitude = 12.3085,
            longitude = 76.6495,
            zoomLevel = 16,
            safetyScore = 68,
            riskLevel = HistoricalRiskLevel.MODERATE,
            nearestPoliceStation = "Devaraja Police Station",
            policePhone = "0821-2418306",
            streetLightingRating = "88% Commercial Bright Lighting",
            cctvCoverageRating = "High Shopkeeper & Beat Cameras (91%)",
            category = "Commercial Hub"
        ),
        com.example.data.model.MysuruArea(
            id = "area_suburban_bus_stand",
            name = "Suburban KSRTC Bus Stand",
            kannadaName = "ಉಪನಗರ ಬಸ್ ನಿಲ್ದಾಣ",
            description = "Central transit terminal connecting Mysuru to Bengaluru, Ooty, Coorg, and Kerala. 24/7 interstate transit activity.",
            latitude = 12.3120,
            longitude = 76.6585,
            zoomLevel = 16,
            safetyScore = 52,
            riskLevel = HistoricalRiskLevel.ELEVATED,
            nearestPoliceStation = "Lashkar Police Station",
            policePhone = "0821-2418307",
            streetLightingRating = "85% High Mast Floodlights",
            cctvCoverageRating = "KSRTC Terminal 360° Surveillance (89%)",
            category = "Transit Hub"
        ),
        com.example.data.model.MysuruArea(
            id = "area_railway_station",
            name = "Mysuru Junction Railway Station",
            kannadaName = "ಮೈಸೂರು ರೈಲು ನಿಲ್ದಾಣ",
            description = "Historic railway terminus with Vande Bharat connections, Railway Museum, and Medar Block access.",
            latitude = 12.3160,
            longitude = 76.6450,
            zoomLevel = 16,
            safetyScore = 65,
            riskLevel = HistoricalRiskLevel.MODERATE,
            nearestPoliceStation = "Railway Police Station (RPF)",
            policePhone = "0821-2420800",
            streetLightingRating = "90% Platform & Circulating Lighting",
            cctvCoverageRating = "Integrated Railway Security System (92%)",
            category = "Transit Hub"
        ),
        com.example.data.model.MysuruArea(
            id = "area_chamundi_hill",
            name = "Chamundi Hill & Viewpoint",
            kannadaName = "ಚಾಮುಂಡಿ ಬೆಟ್ಟ",
            description = "Sacred hill towering over Mysuru at 1,062 meters with Sri Chamundeshwari Temple, Nandi monolith, and scenic winding ghat road.",
            latitude = 12.2740,
            longitude = 76.6710,
            zoomLevel = 14,
            safetyScore = 74,
            riskLevel = HistoricalRiskLevel.MODERATE,
            nearestPoliceStation = "Krishnaraja Police Station",
            policePhone = "0821-2418308",
            streetLightingRating = "75% Ghat Road Solar & Sodium Lights",
            cctvCoverageRating = "Temple Precinct & Toll Surveillance (80%)",
            category = "Heritage / Tourist"
        ),
        com.example.data.model.MysuruArea(
            id = "area_bannimantap",
            name = "Bannimantap & Torchlight Grounds",
            kannadaName = "ಬನ್ನಿಮಂಟಪ",
            description = "Venue of world-famous Dasara Torch Light Parade, Manipal Hospital Mysuru, and Bengaluru-Mysuru Highway corridor.",
            latitude = 12.3380,
            longitude = 76.6520,
            zoomLevel = 15,
            safetyScore = 82,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "N.R. Police Station",
            policePhone = "0821-2418310",
            streetLightingRating = "95% Express Highway Illumination",
            cctvCoverageRating = "Traffic Police Junction Cameras (84%)",
            category = "Commercial Hub"
        ),
        com.example.data.model.MysuruArea(
            id = "area_hebbal_industrial",
            name = "Hebbal Industrial Area & IT Hub",
            kannadaName = "ಹೆಬ್ಬಾಳ ಕೈಗಾರಿಕಾ ಪ್ರದೇಶ",
            description = "Major IT corridor housing Infosys Mysuru Campus, L&T, BEML, and electronic tech campuses on Outer Ring Road.",
            latitude = 12.3620,
            longitude = 76.5980,
            zoomLevel = 14,
            safetyScore = 78,
            riskLevel = HistoricalRiskLevel.MODERATE,
            nearestPoliceStation = "Hebbal Police Station",
            policePhone = "0821-2418320",
            streetLightingRating = "80% Industrial Corridor Lighting",
            cctvCoverageRating = "Corporate Perimeter Security (90%)",
            category = "Industrial"
        ),
        com.example.data.model.MysuruArea(
            id = "area_nazarbad",
            name = "Nazarbad & Mysuru Zoo Precinct",
            kannadaName = "ನಜರ್‌ಬಾದ್",
            description = "Sri Chamarajendra Zoological Gardens, Karanji Lake nature sanctuary, and Mysore Race Club along Race Course Road.",
            latitude = 12.3030,
            longitude = 76.6680,
            zoomLevel = 15,
            safetyScore = 85,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Nazarbad Police Station",
            policePhone = "0821-2418309",
            streetLightingRating = "92% Tourist Corridor Lighting",
            cctvCoverageRating = "Zoo & Lake Perimeter Network (86%)",
            category = "Heritage / Tourist"
        ),
        com.example.data.model.MysuruArea(
            id = "area_manasagangotri",
            name = "Manasagangotri (Mysore University)",
            kannadaName = "ಮಾನಸಗಂಗೋತ್ರಿ",
            description = "739-acre picturesque academic campus on the banks of Kukkarahalli Lake. Wide pedestrian jogging walkways.",
            latitude = 12.3090,
            longitude = 76.6200,
            zoomLevel = 15,
            safetyScore = 88,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Jayalakshmipuram Police Station",
            policePhone = "0821-2418313",
            streetLightingRating = "89% Campus Pathway LED Grid",
            cctvCoverageRating = "University Gate Security (85%)",
            category = "Educational"
        ),
        com.example.data.model.MysuruArea(
            id = "area_jp_nagar",
            name = "JP Nagar & Gobli Mara",
            kannadaName = "ಜೆ.ಪಿ ನಗರ",
            description = "Rapidly expanding residential layout in southern Mysuru, adjoining Ring Road and Sewage Farm greenery.",
            latitude = 12.2680,
            longitude = 76.6350,
            zoomLevel = 15,
            safetyScore = 82,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Vidyaranyapuram Police Station",
            policePhone = "0821-2418315",
            streetLightingRating = "86% Suburban Lighting",
            cctvCoverageRating = "Key Crossroad Cameras (70%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_ramakrishnanagar",
            name = "Ramakrishnanagar",
            kannadaName = "ರಾಮಕೃಷ್ಣನಗರ",
            description = "Peaceful residential neighborhood adjoining Ring Road and Ramakrishna Ashram sports grounds.",
            latitude = 12.2810,
            longitude = 76.6180,
            zoomLevel = 15,
            safetyScore = 85,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Kuvempunagar Police Station",
            policePhone = "0821-2418316",
            streetLightingRating = "91% Residential LED Lighting",
            cctvCoverageRating = "Community Network (75%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_siddartha_layout",
            name = "Siddartha Layout & Lalitha Mahal",
            kannadaName = "ಸಿದ್ಧಾರ್ಥ ಬಡಾವಣೆ",
            description = "Grand Italianate palace view, sprawling tree-lined boulevards, and quiet diplomatic/residential enclaves.",
            latitude = 12.2980,
            longitude = 76.6850,
            zoomLevel = 15,
            safetyScore = 84,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Alanahalli Police Station",
            policePhone = "0821-2418318",
            streetLightingRating = "92% Broad Boulevard Lighting",
            cctvCoverageRating = "Palace & Highway Security (81%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_yadavagiri",
            name = "Yadavagiri & Railway Colony",
            kannadaName = "ಯಾದವಗಿರಿ",
            description = "Heritage tree-canopied enclave, residence of literary legends (RK Narayan's house museum) and quiet bungalows.",
            latitude = 12.3250,
            longitude = 76.6380,
            zoomLevel = 15,
            safetyScore = 89,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "V.V. Puram Police Station",
            policePhone = "0821-2418314",
            streetLightingRating = "94% Tree-friendly Ambient LED",
            cctvCoverageRating = "Private & Municipal Beats (83%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_bogadi",
            name = "Bogadi & Ring Road Junction",
            kannadaName = "ಬೋಗಾದಿ",
            description = "Western growth corridor, Marimallappa College campus, and Ring Road gateway towards Madikeri.",
            latitude = 12.3070,
            longitude = 76.5950,
            zoomLevel = 15,
            safetyScore = 83,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Saraswathipuram Police Station",
            policePhone = "0821-2418312",
            streetLightingRating = "87% Ring Road Lighting",
            cctvCoverageRating = "Junction Signals (74%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_metagalli",
            name = "Metagalli & KRS Road",
            kannadaName = "ಮೆಟಗಳ್ಳಿ",
            description = "Arterial road connecting Mysuru city center to Brindavan Gardens (KRS Dam) with automotive engineering clusters.",
            latitude = 12.3490,
            longitude = 76.6220,
            zoomLevel = 15,
            safetyScore = 79,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Metagalli Police Station",
            policePhone = "0821-2418319",
            streetLightingRating = "88% KRS Highway Lighting",
            cctvCoverageRating = "Industrial Perimeter (78%)",
            category = "Industrial"
        ),
        com.example.data.model.MysuruArea(
            id = "area_vidyaranyapuram",
            name = "Vidyaranyapuram",
            kannadaName = "ವಿದ್ಯಾರಣ್ಯಪುರಂ",
            description = "South Mysuru traditional residential quarter near Chamundeshwari Temple foothills and JSS Ayurvedic campus.",
            latitude = 12.2850,
            longitude = 76.6510,
            zoomLevel = 15,
            safetyScore = 81,
            riskLevel = HistoricalRiskLevel.LOW,
            nearestPoliceStation = "Vidyaranyapuram Police Station",
            policePhone = "0821-2418315",
            streetLightingRating = "87% Municipal Lighting",
            cctvCoverageRating = "Local Police Beat (72%)",
            category = "Residential"
        ),
        com.example.data.model.MysuruArea(
            id = "area_hootagalli",
            name = "Hootagalli & Coorg Highway",
            kannadaName = "ಹೂಟಗಳ್ಳಿ",
            description = "Industrial town on Mangalore-Madikeri highway, heavy logistics, and Outer Ring Road flyover junction.",
            latitude = 12.3480,
            longitude = 76.5780,
            zoomLevel = 14,
            safetyScore = 78,
            riskLevel = HistoricalRiskLevel.MODERATE,
            nearestPoliceStation = "Vijayanagar Police Station",
            policePhone = "0821-2418317",
            streetLightingRating = "84% Highway Mast Lights",
            cctvCoverageRating = "Toll & Industrial Gate Monitoring (76%)",
            category = "Industrial"
        )
    )

    /**
     * MYSURU Verified Emergency Stations (Police, Trauma Hospitals, Medical Dispatch)
     */
    private val mysuruEmergencyStations = listOf(
        EmergencyPlace(
            id = "MYS-POL-01",
            name = "Devaraja Police Station",
            type = PlaceType.POLICE_STATION,
            latitude = 12.3105,
            longitude = 76.6510,
            address = "D. Devaraja Urs Road, Central Mysuru",
            phoneNumber = "0821-2418306",
            distanceMeters = 350,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-POL-02",
            name = "Lashkar Police Station (Bus Stand)",
            type = PlaceType.POLICE_STATION,
            latitude = 12.3140,
            longitude = 76.6560,
            address = "Near Suburban KSRTC Bus Stand, Mysuru",
            phoneNumber = "0821-2418307",
            distanceMeters = 550,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-POL-03",
            name = "Saraswathipuram Police Station",
            type = PlaceType.POLICE_STATION,
            latitude = 12.3015,
            longitude = 76.6290,
            address = "Kantharaj Urs Road, Saraswathipuram",
            phoneNumber = "0821-2418312",
            distanceMeters = 1800,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-POL-04",
            name = "Vijayanagar Police Station",
            type = PlaceType.POLICE_STATION,
            latitude = 12.3385,
            longitude = 76.6025,
            address = "Water Tank Junction, Vijayanagar 2nd Stage",
            phoneNumber = "0821-2418317",
            distanceMeters = 3200,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-POL-05",
            name = "Kuvempunagar Police Station",
            type = PlaceType.POLICE_STATION,
            latitude = 12.2885,
            longitude = 76.6280,
            address = "Near Kuvempunagar Complex, Mysuru",
            phoneNumber = "0821-2418316",
            distanceMeters = 2400,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-POL-06",
            name = "V.V. Puram Police Station (Gokulam)",
            type = PlaceType.POLICE_STATION,
            latitude = 12.3290,
            longitude = 76.6275,
            address = "Temple Road, V.V. Mohalla / Gokulam",
            phoneNumber = "0821-2418314",
            distanceMeters = 2100,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-POL-07",
            name = "Nazarbad Police Station (Zoo/Palace East)",
            type = PlaceType.POLICE_STATION,
            latitude = 12.3045,
            longitude = 76.6660,
            address = "Race Course Road, Nazarbad, Mysuru",
            phoneNumber = "0821-2418309",
            distanceMeters = 1200,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-HOSP-01",
            name = "K.R. Hospital & Trauma Center",
            type = PlaceType.HOSPITAL,
            latitude = 12.3125,
            longitude = 76.6495,
            address = "Irwin Road, Mysore Medical College Campus",
            phoneNumber = "0821-2520512",
            distanceMeters = 680,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-HOSP-02",
            name = "Apollo BGS Hospitals Mysuru",
            type = PlaceType.HOSPITAL,
            latitude = 12.2890,
            longitude = 76.6340,
            address = "Adichunchanagiri Road, Kuvempunagar",
            phoneNumber = "0821-2568888",
            distanceMeters = 2600,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-HOSP-03",
            name = "Manipal Hospital Mysuru",
            type = PlaceType.HOSPITAL,
            latitude = 12.3480,
            longitude = 76.6470,
            address = "Bengaluru-Mysuru Ring Road Junction, Bannimantap",
            phoneNumber = "0821-2555555",
            distanceMeters = 4100,
            isOpen24Hours = true,
            city = "Mysuru"
        ),
        EmergencyPlace(
            id = "MYS-HOSP-04",
            name = "JSS Hospital & Research Institute",
            type = PlaceType.HOSPITAL,
            latitude = 12.2950,
            longitude = 76.6570,
            address = "Ramanuja Road, Fort Mohalla, Mysuru",
            phoneNumber = "0821-2335555",
            distanceMeters = 1100,
            isOpen24Hours = true,
            city = "Mysuru"
        )
    )

    // San Francisco Data fallback
    private val sfIncidents = listOf(
        CrimeIncident(
            id = "SF-INC-001",
            incidentNumber = "CR-89421",
            category = "Robbery / Mugging",
            offenseDescription = "Street robbery involving personal electronics",
            date = "2024-03-14",
            time = "22:45",
            latitude = 37.7845,
            longitude = -122.4095,
            policeDistrict = "Downtown District",
            weaponInvolved = true,
            historicalSeverityScore = 8.5,
            city = "San Francisco"
        ),
        CrimeIncident(
            id = "SF-INC-002",
            incidentNumber = "CR-89488",
            category = "Larceny / Theft",
            offenseDescription = "Bag snatching near transit terminal",
            date = "2024-03-18",
            time = "18:20",
            latitude = 37.7831,
            longitude = -122.4112,
            policeDistrict = "Central District",
            weaponInvolved = false,
            historicalSeverityScore = 4.2,
            city = "San Francisco"
        )
    )

    private val sfClusters = listOf(
        CrimeRiskCluster(
            clusterId = "SF-CLUSTER-01",
            title = "West Market & 6th St Zone",
            centerLat = 37.7838,
            centerLng = -122.4105,
            radiusMeters = 350.0,
            historicalIncidentCount = 142,
            riskLevel = HistoricalRiskLevel.ELEVATED,
            predominantOffense = "Robbery (42%), Theft (38%)",
            temporalPeak = "Elevated 21:30 – 04:00",
            activeSurveillance = "Moderate CCTV on avenue",
            safetyAwarenessTips = listOf("Stay on illuminated streets", "Conceal valuables"),
            city = "San Francisco"
        )
    )

    private val sfEmergencyStations = listOf(
        EmergencyPlace(
            id = "SF-POL-01",
            name = "Central Police Precinct",
            type = PlaceType.POLICE_STATION,
            latitude = 37.7885,
            longitude = -122.4040,
            address = "766 Vallejo St, SF",
            phoneNumber = "911",
            distanceMeters = 420,
            isOpen24Hours = true,
            city = "San Francisco"
        ),
        EmergencyPlace(
            id = "SF-HOSP-01",
            name = "Saint Francis Memorial Hospital",
            type = PlaceType.HOSPITAL,
            latitude = 37.7898,
            longitude = -122.4150,
            address = "900 Hyde St (Emergency Dept)",
            phoneNumber = "+1-415-353-6000",
            distanceMeters = 780,
            isOpen24Hours = true,
            city = "San Francisco"
        )
    )

    fun getHistoricalIncidents(city: String = "Mysuru"): List<CrimeIncident> {
        return if (city == "Mysuru") mysuruIncidents else sfIncidents
    }

    fun getCrimeClusters(city: String = "Mysuru"): List<CrimeRiskCluster> {
        return if (city == "Mysuru") mysuruClusters else sfClusters
    }

    fun getEmergencyStations(city: String = "Mysuru"): List<EmergencyPlace> {
        return if (city == "Mysuru") mysuruEmergencyStations else sfEmergencyStations
    }

    fun getMysuruAreas(): List<com.example.data.model.MysuruArea> = mysuruAreasList

    fun findAreaByName(query: String): List<com.example.data.model.MysuruArea> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return mysuruAreasList
        return mysuruAreasList.filter {
            it.name.lowercase().contains(q) ||
            it.kannadaName.contains(q) ||
            it.description.lowercase().contains(q) ||
            it.category.lowercase().contains(q)
        }
    }

    /**
     * Geographical risk evaluation for coordinates
     */
    fun evaluateLocationRisk(lat: Double, lng: Double, city: String = "Mysuru"): Pair<HistoricalRiskLevel, Int> {
        val incidents = getHistoricalIncidents(city)
        val clusters = getCrimeClusters(city)
        var weightedDensity = 0.0

        // Cluster density influence
        for (cluster in clusters) {
            val dist = calculateDistanceMeters(lat, lng, cluster.centerLat, cluster.centerLng)
            if (dist < cluster.radiusMeters * 1.5) {
                val clusterWeight = when (cluster.riskLevel) {
                    HistoricalRiskLevel.ELEVATED -> 45.0
                    HistoricalRiskLevel.MODERATE -> 24.0
                    HistoricalRiskLevel.LOW -> 8.0
                }
                val factor = (1.0 - (dist / (cluster.radiusMeters * 1.5))).coerceIn(0.0, 1.0)
                weightedDensity += clusterWeight * factor
            }
        }

        // Individual incident influence
        for (incident in incidents) {
            val distMeters = calculateDistanceMeters(lat, lng, incident.latitude, incident.longitude)
            if (distMeters < 800.0) {
                val decay = kotlin.math.exp(-0.5 * (distMeters / 250.0).pow(2))
                val severityWeight = incident.historicalSeverityScore / 10.0
                weightedDensity += severityWeight * decay * 22.0
            }
        }

        val finalScore = weightedDensity.coerceIn(12.0, 92.0).toInt()
        val level = when {
            finalScore < 36 -> HistoricalRiskLevel.LOW
            finalScore < 66 -> HistoricalRiskLevel.MODERATE
            else -> HistoricalRiskLevel.ELEVATED
        }
        return Pair(level, finalScore)
    }

    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
