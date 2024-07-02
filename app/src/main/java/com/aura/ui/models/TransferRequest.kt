package com.aura.ui.models


data class TransferRequest(
    val senderId: String,
    val recipientId: String,
    val amount: Double
)

