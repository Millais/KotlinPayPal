package com.kotlin.patrick.paypal.models

data class ExecutePaymentResponse(
        val id: String,
        val intent: String,
        val state: String,
        val cart: String,
        val payer: Payer,
        val transactions: List<Transaction>,
        val createTime: String,
        val links: List<Link>
)

// Amount, Item, Link & ItemList have existing definitions in CreatePaymentResponse

class BillingAddress {
    var line1: String? = null
    var line2: String? = null
    var city: String? = null
    var state: String? = null
    var postalCode: String? = null
    var countryCode: String? = null
}

class Payee {
    var merchantId: String? = null
    var email: String? = null
}

// Payer adds additional fields

//public class Payer {
//
//    public String paymentMethod;
//    public String status;
//    public PayerInfo payerInfo;
//
//}

class PayerInfo {
    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var payerId: String? = null
    var shippingAddress: ShippingAddress? = null
    var phone: String? = null
    var countryCode: String? = null
    var billingAddress: BillingAddress? = null
}

class RelatedResource {
    var sale: Sale? = null
}

class Sale {
    var id: String? = null
    var state: String? = null
    var amount: Amount? = null
    var paymentMode: String? = null
    var protectionEligibility: String? = null
    var protectionEligibilityType: String? = null
    var transactionFee: TransactionFee? = null
    var parentPayment: String? = null
    var createTime: String? = null
    var updateTime: String? = null
    var links: List<Link>? = null
    var softDescriptor: String? = null
}

class ShippingAddress {
    var recipientName: String? = null
    var line1: String? = null
    var city: String? = null
    var state: String? = null
    var postalCode: String? = null
    var countryCode: String? = null
}

//public class Transaction {
//
//    var amount : Amount? = null
//    var payee : Payee? = null
//    var description : String? = null
//    var invoiceNumber : String? = null
//    var itemList : ItemList? = null
//    var relatedResources : List<RelatedResource>? = null
//
//}

class TransactionFee {
    var value: String? = null
    var currency: String? = null
}


