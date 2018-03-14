package com.kotlin.patrick.paypal

import android.content.Intent
import com.kotlin.patrick.paypal.models.CreatePaymentResponse
import com.kotlin.patrick.paypal.models.ExecutePaymentResponse

class PayPalPresenter(private var controller: PayPalController) : PayPalContract.Presenter, PayPalContract.Network {

    private var network = PayPalNetwork(this)

    override fun getAccessToken() {
        network.getAccessToken()
        controller.startAccessTokenProgressBar()
    }

    override fun onAccessTokenSuccess(response: String?) {
        controller.showAccessToken(response)
        controller.stopAccessTokenProgressBar()
        beginPayment()
    }

    override fun onAccessTokenFailure() {
        controller.showAccessTokenErrorMessage()
        controller.stopAccessTokenProgressBar()
    }

    override fun onCreatePaymentSuccess(response: CreatePaymentResponse?) {
        controller.stopCreatePaymentProgressBar()
        if (response?.id != null) controller.showCreatePaymentID(response.id)

        // Set button redirect from response links
        if (response?.links != null) {
            for (link in response.links) {
                if (link.method == "REDIRECT") {
                    controller.setPayPalRedirect(link.href + "&useraction=commit")
                    controller.showPayPalButton()
                }
            }
        }
    }

    override fun onCreatePaymentFailure() {
        controller.showCreatePaymentErrorMessage()
        controller.stopCreatePaymentProgressBar()
    }

    override fun onExecutePaymentSuccess(response: ExecutePaymentResponse?) {
        controller.stopExecutePaymentProgressBar()
        controller.showExecutePaymentSuccess("State: " + response?.state)
        controller.showStartAgainButton()
    }

    override fun onExecutePaymentFailure() {
        controller.stopExecutePaymentProgressBar()
        controller.showExecutePaymentErrorMessage()
        controller.showStartAgainButton()
    }

    override fun beginPayment() {
        network.createPayment()
        controller.showCreatePaymentCard()
        controller.startCreatePaymentProgressBar()
    }

    override fun finalisePayment(id: String, payerId: String) {
        controller.showExecutePaymentCard()
        controller.startExecutePaymentProgressBar()
        network.finalisePayment(id, payerId)
    }

    // When returning by deep link, finalise the payment using the intent data
    fun onCheckoutReturn(intent: Intent) {
        if (intent.data.getQueryParameter("paymentId") != null) {
            finalisePayment(intent.data.getQueryParameter("paymentId"),
                    intent.data.getQueryParameter("PayerID"))
        }
    }
}