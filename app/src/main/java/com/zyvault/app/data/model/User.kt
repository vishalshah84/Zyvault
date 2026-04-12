package com.zyvault.app.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val plan: String = "Personal",
    val documentCount: Int = 0,
    val bankAccountCount: Int = 0,
    val billsDueCount: Int = 0,
    val totalSaved: Double = 0.0,
    val notificationCount: Int = 0
)
