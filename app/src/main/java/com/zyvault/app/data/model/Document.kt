package com.zyvault.app.data.model

import com.google.firebase.Timestamp

data class Document(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val expiryDate: String = "",
    val category: String = "",
    val status: String = "Valid",
    val fileUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
