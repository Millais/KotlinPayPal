var express = require('express');
var app = express();
var braintree = require("braintree");
var bodyParser = require("body-parser");

var gateway = braintree.connect({
  accessToken: "<Sandbox Access Token>"
});

app.use(bodyParser.json());

app.get("/client_token", function (req, res) {
  gateway.clientToken.generate({}, function (err, response) {
    res.send({"clientToken": response.clientToken});
  });
});

app.post("/checkout", function (req, res) {

	var nonce = req.body.nonce;
	// Process a basic example transaction
	gateway.transaction.sale({
		amount: "20.00",
		paymentMethodNonce: nonce,
		options: {
			submitForSettlement: true
		}
		}, function (err, result) {
			if (result.success){
				res.send({"state": "Success"});
			}else{
				res.send({"state": "Failure"})
			}
		});
});

app.listen(12345, () => console.log('Basic Braintree server running (Port 12345)'))