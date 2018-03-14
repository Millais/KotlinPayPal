package com.kotlin.patrick.braintree

import com.braintreepayments.api.models.PaymentMethodNonce
import com.kotlin.patrick.ConfigSamplesHelper

class BraintreePresenter(private var controller: BraintreeController) : BraintreeContract.Presenter, BraintreeContract.Network {

    private var network = BraintreeNetwork(this)

    override fun getClientToken() {
        network.getClientToken()
        controller.startClientTokenProgressBar()
    }

    override fun onClientTokenSuccess(clientToken: String?) {
        controller.showClientToken(clientToken)
        controller.stopClientTokenProgressBar()
        controller.showNonceCard()
    }

    override fun onClientTokenFailure() {
        controller.showClientTokenErrorMessage()
        controller.stopClientTokenProgressBar()
    }

    override fun onCheckoutSuccess() {
        controller.showPaymentSuccess()
        controller.stopCheckoutProgressBar()
        controller.showStartAgainButton()
    }

    override fun onCheckoutFailure() {
        controller.showClientTokenErrorMessage()
        controller.stopCheckoutProgressBar()
    }

    override fun openPayPal() {
        val clientToken = network.clientToken
        if (clientToken == null) {
            controller.showPayPalButtonError()
        } else {
            // Use sample Braintree PayPal request
            controller.loadBraintreeFragment(clientToken, ConfigSamplesHelper.getBraintreePayPalRequestExample())
        }
    }

    override fun finalisePayment(nonce: PaymentMethodNonce) {
        if (nonce.nonce != null) {
            controller.showNonce(nonce.nonce)
            controller.startCheckoutProgressBar()
            controller.showNonceContents()
            controller.showCheckoutCard()
            // Process payment
            network.checkoutPayment(nonce.nonce)
        } else {
            controller.showNonceErrorMessage()
        }
    }
}