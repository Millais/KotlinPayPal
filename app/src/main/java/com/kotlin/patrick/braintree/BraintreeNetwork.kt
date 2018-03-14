package com.kotlin.patrick.braintree

import android.util.Log
import com.kotlin.patrick.ConfigSamplesHelper
import com.kotlin.patrick.braintree.models.CheckoutResponse
import com.kotlin.patrick.braintree.models.ClientTokenResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BraintreeNetwork(var presenter: BraintreePresenter) {

    val api by lazy { BraintreeService.create() }
    var clientToken: String? = null

    fun getClientToken() {

        val call = api.getClientToken()

        call.enqueue(object : Callback<ClientTokenResponse> {
            override fun onResponse(call: Call<ClientTokenResponse>?, response:
            Response<ClientTokenResponse>?) =
                if (response != null && response.isSuccessful) {
                    clientToken = response.body()?.clientToken
                    presenter.onClientTokenSuccess(clientToken)
                } else {
                    Log.d("Braintree", "Fail: Bad response\n" + response?.body().toString())
                    presenter.onClientTokenFailure()
                }

            override fun onFailure(call: Call<ClientTokenResponse>?, t: Throwable?) {
                Log.d("Braintree", "Fail: No response\n" + t.toString())
                presenter.onClientTokenFailure()
            }
        }
        )
    }

    fun checkoutPayment(nonce: String) {

        val call = api.checkoutPayment(ConfigSamplesHelper.getJSON("{\"nonce\": \"$nonce\"}"))

        call.enqueue(object : Callback<CheckoutResponse> {
            override fun onResponse(call: Call<CheckoutResponse>?, response:
            Response<CheckoutResponse>?) =
                if (response != null && response.isSuccessful && response.body()?.state == "Success") {
                    Log.d("Braintree", "Success: " + response.body())
                    presenter.onCheckoutSuccess()
                } else {
                    Log.d("Braintree", "Fail: Bad response\n" + response?.body())
                    presenter.onCheckoutFailure()
                }

            override fun onFailure(call: Call<CheckoutResponse>?, t: Throwable?) {
                Log.d("Braintree", "Fail: No response\n" + t.toString())
                presenter.onCheckoutFailure()
            }
        }
        )
    }
}