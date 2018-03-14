package com.kotlin.patrick

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.PayPal
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.PayPalRequest
import com.braintreepayments.api.models.PaymentMethodNonce
import com.google.gson.Gson
import com.kotlin.patrick.braintree.BraintreeController
import com.kotlin.patrick.paypal.PayPalController
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), PaymentMethodNonceCreatedListener {

    private lateinit var router: Router

    companion object {
        val CUSTOM_TAB_RETURN = 1
        val BRAINTREE_RETURN = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setToolbarAndNavigationDrawer()
        router = Conductor.attachRouter(this, container, savedInstanceState)
        resetMainController()
    }


    private fun setToolbarAndNavigationDrawer() {
        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close)

        themeController(ContextCompat.getColor(applicationContext, R.color.colorPayPalTwo))
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_paypal -> switchController(it.title as String)
                R.id.action_braintree -> switchController(it.title as String)
                R.id.action_open_source -> showOpenSourceLicenses()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    fun resetMainController() {
        if (router.hasRootController()) {
            // Restart the active controller
            when (router.backstack[0].controller()) {
                is PayPalController -> switchController(resources.getString(R.string.action_paypal))
                is BraintreeController -> switchController(resources.getString(R.string.action_braintree))
            }
        } else {
            // Show the default controller
            switchController(resources.getString(R.string.action_paypal))
        }
    }

    private fun switchController(title: String) {
        toolbar.subtitle = title

        if (router.hasRootController()) router.popCurrentController()

        if (title == resources.getString(R.string.action_paypal)) {
            themeController(ContextCompat.getColor(applicationContext, R.color.colorPayPalTwo))
            setPayPalController()
        } else if (title == resources.getString(R.string.action_braintree)) {
            themeController(ContextCompat.getColor(applicationContext, R.color.colorPayPalNightOne))
            setBraintreeController()
        }
    }

    private fun themeController(color: Int) {
        drawerLayout.setStatusBarBackgroundColor(color)
        toolbar.setBackgroundColor(color)
    }

    private fun setPayPalController() {
        val payPalController = PayPalController().apply { registerForActivityResult(CUSTOM_TAB_RETURN) }
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(payPalController))
        }
    }

    private fun setBraintreeController() {
        val braintreeController = BraintreeController().apply { registerForActivityResult(BRAINTREE_RETURN) }
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(braintreeController))
        }
    }

    private fun showOpenSourceLicenses() {
        drawerLayout.closeDrawers()
        val openSourceLibaries = WebView(applicationContext)
        ConfigSamplesHelper.getOpenSourceText()
        openSourceLibaries.loadData(ConfigSamplesHelper.getOpenSourceText(),
                "text/html", "utf-8")
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(openSourceLibaries)
                .setNeutralButton("Close", null)
                .show()
    }

    // Load the PayPal Checkout via the Braintree SDK
    fun loadBraintreeFragment(clientToken: String, request: PayPalRequest) {
        try {
            val braintreeFragment = BraintreeFragment.newInstance(this, clientToken)
            PayPal.requestOneTimePayment(braintreeFragment, request)
        } catch (e: InvalidArgumentException) {
            Toast.makeText(applicationContext, resources.getString(R.string.bt_load_error),
                    Toast.LENGTH_SHORT).show()
        }
    }

    // Capture the deep link through the Custom Tab return intent
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            // Send intent to the controller to handle
            router.onActivityResult(CUSTOM_TAB_RETURN, 1, intent)
        } else {
            Log.d("PayPal", "The PayPal return intent is null.")
        }
    }

    // Capture the nonce using the Braintree SDK callback
    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        // Parse nonce, add to intent and send to the controller to handle
        intent.putExtra("nonce", Gson().toJson(paymentMethodNonce))
        router.onActivityResult(BRAINTREE_RETURN, 2, intent)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawers()
        else super.onBackPressed()
    }
}
