package com.kotlin.patrick.braintree

import com.braintreepayments.api.models.PaymentMethodNonce
import com.kotlin.patrick.BaseViewContract

interface BraintreeContract {

    // Details the contract between view and presenter (and network)

    interface View : BaseViewContract {
        fun showClientToken(clientToken: String?)
        fun showClientTokenErrorMessage()
        fun showNonce(nonce: String?)
        fun showNonceErrorMessage()
        fun showPaymentSuccess()
        fun showPaymentErrorMessage()

        fun startClientTokenProgressBar()
        fun stopClientTokenProgressBar()
        fun startCheckoutProgressBar()
        fun stopCheckoutProgressBar()

        fun showNonceCard()
        fun showNonceContents()
        fun showCheckoutCard()
    }

    interface Presenter {
        fun getClientToken()
        fun openPayPal()
        fun finalisePayment(nonce: PaymentMethodNonce)
    }

    interface Network {
        fun onClientTokenSuccess(clientToken: String?)
        fun onClientTokenFailure()
        fun onCheckoutSuccess()
        fun onCheckoutFailure()
    }
}