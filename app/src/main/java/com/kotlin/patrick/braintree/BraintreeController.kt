package com.kotlin.patrick.braintree

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PayPalRequest
import com.google.gson.Gson
import com.kotlin.patrick.MainActivity
import com.kotlin.patrick.R
import kotlinx.android.synthetic.main.braintree_main.view.*

class BraintreeController : Controller(), BraintreeContract.View {

    private lateinit var presenter: BraintreePresenter
    internal lateinit var view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        // Layout requires caching to access synthetic properties outside of Activities/Fragments
        view = inflater.inflate(R.layout.braintree_main, container, false)
        setPresenter()
        return view
    }

    override fun setPresenter() {
        presenter = BraintreePresenter(this)
        presenter.getClientToken()

        view.btnOpenPayPal.setOnClickListener {
            presenter.openPayPal()
        }
        view.btnStartAgain.setOnClickListener {
            (activity as MainActivity).resetMainController()
        }
    }

    // Call the main activity to launch the PayPal checkout
    fun loadBraintreeFragment(clientToken: String, request: PayPalRequest) {
        (activity as MainActivity).loadBraintreeFragment(clientToken, request)
    }

    // IMPORTANT: Intent *must* be specified as nullable
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == MainActivity.BRAINTREE_RETURN && resultCode == 2) {
            if (intent != null) {
                val nonce = Gson().fromJson<PayPalAccountNonce>(
                        intent.getStringExtra("nonce"),
                        PayPalAccountNonce::class.java)
                presenter.finalisePayment(nonce)
            }
        }
    }

    override fun showClientToken(clientToken: String?) {
        view.txtClientToken.text = clientToken
    }

    override fun showClientTokenErrorMessage() {
        view.txtClientToken.text = resources?.getText(R.string.bt_client_token_error)
    }

    override fun showNonce(nonce: String?) {
        view.txtNonce.text = nonce
    }

    override fun showNonceErrorMessage() {
        view.txtNonce.text = resources?.getText(R.string.bt_nonce_error)
    }

    override fun showPaymentSuccess() {
        // Pass the empty string for a successful transaction through Braintree
        view.txtFinalResult.text = resources?.getString(R.string.success, "")
    }

    override fun showPaymentErrorMessage() {
        view.txtFinalResult.text = resources?.getText(R.string.bt_result_error)
    }

    override fun startClientTokenProgressBar() {
        view.pgsBarClientToken.visibility = View.VISIBLE
    }

    override fun stopClientTokenProgressBar() {
        view.pgsBarClientToken.visibility = View.GONE
    }

    override fun startCheckoutProgressBar() {
        view.pgsBarNonce.visibility = View.VISIBLE
    }

    override fun stopCheckoutProgressBar() {
        view.pgsBarNonce.visibility = View.GONE
    }

    override fun showStartAgainButton() {
        view.btnStartAgain.visibility = View.VISIBLE
    }

    override fun showNonceCard() {
        view.crdStep2.visibility = View.VISIBLE
    }

    override fun showNonceContents() {
        view.txtNonce.visibility = View.VISIBLE
    }

    override fun showCheckoutCard() {
        view.crdStep3.visibility = View.VISIBLE
    }

    fun showPayPalButtonError() {
        Toast.makeText(this.applicationContext, resources?.getString(R.string.bt_client_token_error), Toast
                .LENGTH_SHORT).show()
    }
}