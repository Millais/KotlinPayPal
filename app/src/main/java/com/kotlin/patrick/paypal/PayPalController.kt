package com.kotlin.patrick.paypal

import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.kotlin.patrick.MainActivity
import com.kotlin.patrick.R
import kotlinx.android.synthetic.main.paypal_main.view.*

class PayPalController : Controller(), PayPalContract.View {

    private lateinit var presenter: PayPalPresenter
    internal lateinit var view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        // Store layout to access synthetic properties outside of Activities/Fragments
        view = inflater.inflate(R.layout.paypal_main, container, false)
        setPresenter()
        return view
    }

    override fun setPresenter() {
        presenter = PayPalPresenter(this)
        presenter.getAccessToken()

        view.btnOpenPayPal.setOnClickListener {
            Toast.makeText(this.applicationContext, resources?.getString(R.string.pwpp_button_uninitialised), Toast.LENGTH_SHORT).show()
        }
        view.btnStartAgain.setOnClickListener {
            (activity as MainActivity).resetMainController()
        }
    }

    override fun setPayPalRedirect(redirectURL: String?) {
        view.btnOpenPayPal.setOnClickListener {
            val customTabIntent = CustomTabsIntent.Builder().build()
            customTabIntent.launchUrl(this.applicationContext, Uri.parse(redirectURL))
        }
    }

    // Capture the deep link return and pass to the presenter to handle
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == MainActivity.CUSTOM_TAB_RETURN && resultCode == 1) {
            if (intent != null) {
                Log.d("PayPal", intent.toString())
                presenter.onCheckoutReturn(intent)
            }
        }
    }

    override fun showAccessToken(accessToken: String?) {
        view.txtAccessToken.text = accessToken
    }

    override fun showAccessTokenErrorMessage() {
        view.txtAccessToken.text = resources?.getText(R.string.pp_access_token_error)
    }

    override fun startAccessTokenProgressBar() {
        view.pgsBarAccessToken.visibility = View.VISIBLE
    }

    override fun stopAccessTokenProgressBar() {
        view.pgsBarAccessToken.visibility = View.GONE
    }

    override fun showCreatePaymentID(paymentId: String?) {
        view.txtCreatePayment.text = paymentId
    }

    override fun showCreatePaymentErrorMessage() {
        view.txtCreatePayment.text = resources?.getText(R.string.pp_create_payment_error)
    }

    override fun startCreatePaymentProgressBar() {
        view.pgsBarCreatePayment.visibility = View.VISIBLE
    }

    override fun stopCreatePaymentProgressBar() {
        view.pgsBarCreatePayment.visibility = View.GONE
    }

    override fun showExecutePaymentErrorMessage() {
        view.txtFinalisePayment.text = resources?.getText(R.string.pp_execute_payment_error)
    }

    override fun showExecutePaymentSuccess(result: String?) {
        view.txtFinalisePayment.text = resources?.getString(R.string.success, result)
    }

    override fun startExecutePaymentProgressBar() {
        view.pgsBarExecutePayment.visibility = View.VISIBLE
    }

    override fun stopExecutePaymentProgressBar() {
        view.pgsBarExecutePayment.visibility = View.GONE
    }

    override fun showCreatePaymentCard() {
        view.crdStep2.visibility = View.VISIBLE
    }

    override fun showExecutePaymentCard() {
        view.crdStep3.visibility = View.VISIBLE
    }

    override fun showPayPalButton() {
        view.btnOpenPayPal.visibility = View.VISIBLE
    }

    override fun showStartAgainButton() {
        view.btnStartAgain.visibility = View.VISIBLE
    }

}