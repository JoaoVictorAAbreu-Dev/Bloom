package com.bloom.app.domain.model

sealed class AppError(
    val userMessage: String,
) {
    data object AiUnavailable : AppError("Bloom Coach is unavailable right now. Local guidance is still available.")
    data object InvalidInput : AppError("Please review the information and try again.")
    data object StorageUnavailable : AppError("Local storage is unavailable right now.")
}
