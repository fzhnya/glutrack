package com.example.profile

data class ApiResponse(
    val success: Boolean,
    val status: String,
    val message: String,
    val user_data: Biodata
)

