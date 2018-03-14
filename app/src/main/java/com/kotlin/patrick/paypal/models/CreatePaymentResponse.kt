package com.kotlin.patrick.paypal.models

data class CreatePaymentResponse(
        val id: String,
        val intent: String,
        val state: String,
        val payer: Payer,
        val transaction: List<Transaction>,
        val createTime: String,
        val links: List<Link>
)

class Amount {
    var total: String? = null
    var currency: String? = null
}

class Item {
    var name: String? = null
    var sku: String? = null
    var description: String? = null
    var price: String? = null
    var currency: String? = null
    var quantity: Int? = null
}

class ItemList {
    var items: List<Item>? = null
}

class Link {
    var href: String? = null
    var rel: String? = null
    var method: String? = null
}

class Payer {
    var paymentMethod: String? = null
}

class PaymentOptions {
    var allowedPaymentMethod: String? = null
    var recurringFlag: Boolean? = null
    var skipFmf: Boolean? = null
}

class Transaction {
    var amount: Amount? = null
    var description: String? = null
    var invoiceNumber: String? = null
    var paymentOptions: PaymentOptions? = null
    var itemList: ItemList? = null
    var relatedResources: List<Any>? = null
}