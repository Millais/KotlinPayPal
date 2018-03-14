package com.kotlin.patrick

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.braintreepayments.api.models.PayPalRequest
import java.text.ParseException

class ConfigSamplesHelper {
    companion object {
        private val parser = Parser()

        fun getJSON(text: String): JsonObject {
            val jsonString = StringBuilder(text)
            return parser.parse(jsonString) as JsonObject
        }

        private fun readFile(resource: Int): String {
            return KotlinApplication.appContext.resources.openRawResource(resource).bufferedReader().use { it.readText() }
        }

        fun getConfigValue(value: String): String {
            val json: JsonObject = getJSON(readFile(R.raw.config))
            return json.string(value) ?: throw ParseException("Error parsing config: " + value, 0)
        }

        fun getOpenSourceText(): String {
            return readFile(R.raw.libraries)
        }

        fun getCreatePaymentExample(): String {
            return readFile(R.raw.create_payment_example)
        }

        fun getBraintreePayPalRequestExample(): PayPalRequest {
            return PayPalRequest("20")
                    .currencyCode("GBP")
                    .intent(PayPalRequest.INTENT_SALE)
                    .userAction(PayPalRequest.USER_ACTION_COMMIT)
        }
    }
}