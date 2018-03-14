package com.kotlin.patrick.paypal

import com.kotlin.patrick.BaseViewContract
import com.kotlin.patrick.paypal.models.CreatePaymentResponse
import com.kotlin.patrick.paypal.models.ExecutePaymentResponse

interface PayPalContract {

    // Details the contract between view and presenter (and network)

    interface View : BaseViewContract {
        fun showAccessToken(result: String?)
        fun showAccessTokenErrorMessage()
        fun showCreatePaymentID(result: String?)
        fun showCreatePaymentErrorMessage()
        fun showExecutePaymentSuccess(result: String?)
        fun showExecutePaymentErrorMessage()

        fun startAccessTokenProgressBar()
        fun startCreatePaymentProgressBar()
        fun startExecutePaymentProgressBar()
        fun stopAccessTokenProgressBar()
        fun stopCreatePaymentProgressBar()
        fun stopExecutePaymentProgressBar()

        fun showCreatePaymentCard()
        fun showExecutePaymentCard()
        fun setPayPalRedirect(result: String?)
        fun showPayPalButton()
    }

    interface Presenter {
        fun getAccessToken()
        fun beginPayment()
        fun finalisePayment(id: String, payerId: String)
    }

    interface Network {
        fun onAccessTokenSuccess(response: String?)
        fun onAccessTokenFailure()
        fun onCreatePaymentSuccess(response: CreatePaymentResponse?)
        fun onCreatePaymentFailure()
        fun onExecutePaymentSuccess(response: ExecutePaymentResponse?)
        fun onExecutePaymentFailure()
    }
}