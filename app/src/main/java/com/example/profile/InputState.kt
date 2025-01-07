package com.example.profile

sealed class InputState {
    object Loading : InputState()
    data class Success(val response: ApiResponse) : InputState()
    data class Error(val message: String) : InputState()
}