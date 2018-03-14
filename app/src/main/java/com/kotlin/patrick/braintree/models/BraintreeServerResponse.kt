package com.kotlin.patrick.braintree.models

data class ClientTokenResponse(
        val clientToken: String
)

data class CheckoutResponse(
        val state: String
)