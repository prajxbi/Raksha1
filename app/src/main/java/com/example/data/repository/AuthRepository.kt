package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("raksha_user_session", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        val email = prefs.getString("user_email", null)
        if (!email.isNullOrBlank()) {
            val name = prefs.getString("user_name", "Rajesh Kumar") ?: "Rajesh Kumar"
            val phone = prefs.getString("user_phone", "+91 98450 12345") ?: "+91 98450 12345"
            val role = prefs.getString("user_role", "Mysuru Citizen") ?: "Mysuru Citizen"
            val city = prefs.getString("user_city", "Mysuru") ?: "Mysuru"

            val user = UserAccount(
                id = "USR-${email.hashCode()}",
                name = name,
                email = email,
                phone = phone,
                userRole = role,
                homeCity = city
            )
            _currentUser.value = user
            _isLoggedIn.value = true
        } else {
            // Default demo logged-in user so features are immediately usable
            quickLogin("Mysuru Citizen")
        }
    }

    fun login(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.length < 4) return false
        val name = email.substringBefore("@").replace(".", " ").capitalizeWords()
        val user = UserAccount(
            id = "USR-${email.hashCode()}",
            name = if (name.isNotBlank()) name else "Verified Citizen",
            email = email,
            phone = "+91 98450 56789",
            userRole = "Mysuru Citizen",
            homeCity = "Mysuru"
        )
        saveUser(user)
        return true
    }

    fun register(name: String, email: String, phone: String, role: String, city: String = "Mysuru"): Boolean {
        if (name.isBlank() || email.isBlank()) return false
        val user = UserAccount(
            id = "USR-${email.hashCode()}",
            name = name,
            email = email,
            phone = if (phone.isNotBlank()) phone else "+91 98450 00000",
            userRole = role,
            homeCity = city
        )
        saveUser(user)
        return true
    }

    fun quickLogin(role: String) {
        val user = when (role) {
            "Student Commuter" -> UserAccount(
                id = "USR-MYS-002",
                name = "Ananya Gowda",
                email = "ananya.gowda@uni-mysore.ac.in",
                phone = "+91 98450 78912",
                userRole = "Student Commuter (University of Mysore)",
                emergencyNote = "Carries daily bus pass; travels Gokulam to Manasagangotri",
                homeCity = "Mysuru"
            )
            "Emergency Responder" -> UserAccount(
                id = "USR-MYS-003",
                name = "Dr. Vikram Rao",
                email = "vikram.rao@krhospital.kar.gov.in",
                phone = "+91 94480 34567",
                userRole = "Emergency Medical Responder (K.R. Hospital)",
                emergencyNote = "Emergency duty physician. On-call for trauma dispatch.",
                homeCity = "Mysuru"
            )
            else -> UserAccount(
                id = "USR-MYS-001",
                name = "Rajesh Kumar",
                email = "rajesh.mysore@gmail.com",
                phone = "+91 98450 12345",
                userRole = "Mysuru Resident & Commuter",
                emergencyNote = "Blood group B+. Resides near Saraswathipuram.",
                homeCity = "Mysuru"
            )
        }
        saveUser(user)
    }

    fun logout() {
        prefs.edit().clear().apply()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    private fun saveUser(user: UserAccount) {
        prefs.edit()
            .putString("user_email", user.email)
            .putString("user_name", user.name)
            .putString("user_phone", user.phone)
            .putString("user_role", user.userRole)
            .putString("user_city", user.homeCity)
            .apply()
        _currentUser.value = user
        _isLoggedIn.value = true
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}
