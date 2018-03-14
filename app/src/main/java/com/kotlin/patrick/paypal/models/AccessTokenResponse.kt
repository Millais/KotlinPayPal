package com.kotlin.patrick.paypal.models

data class AccessTokenResponse(
        val access_token: String,
        val scope: String,
        val nonce: String,
        val token_type: String,
        val app_id: String,
        val expires_in: Int
)