//Auth.js

function AuthAccess(code)
{
	this.authcode = code;
	this.urlParams = "?authcode=" + this.authcode;
	this.verify();
}

AuthAccess.prototype = {
	constructor: AuthAccess,

	APIGET: function(loc, success, fail) {
		$.ajax({
			url: "../API/" + loc + this.urlParams,
			dataType: "json",
			success: function(data) {
				if(data.status == "Success") {
					success(data);
				} else {
					fail(data);
				}
			},
			failure: function() {
				fail();
			}
		});
	},

	APIPOST: function(loc, jdata, success, fail) {
		$.ajax({
			type: "POST",
			url: "../API/" + loc + this.urlParams,
			dataType: "json",
			data: jdata,
			success: function(data) {
				if(data.status == "Success") {
					success(data);
				} else {
					fail(data);
				}
			},
			failure: function() {
				fail();
			}
		});
	},


	verify: function() {
		this.APIGET("verify", function() {}, function() {
			window.location.href = "login.html";
		});
	},

	logout: function() {
		this.APIGET("logout", function() {
			window.location.href = "login.html";
		}, function() { });
	},

	getUsername: function(fun) {
		this.APIGET("username", function(data) {
			fun(data.message);
		}, function () {});
	},

	getAccounts: function(fun) {
		this.APIGET("bank/accounts", function(data) {
			fun(data.message);
		}, function () {});
	},

	getTransfers: function(fun) {
		this.APIGET("bank/transfers", function(data) {
			fun(data.message);
		}, function() {});
	},

	getPhones: function(fun) {
		this.APIGET("carrier/phones", function(data) {
			fun(data.message);
		}, function() {});
	},

	createUsernameTransfer: function(acid, amount, username, success, fail) {
		var obj = {
			accountid: acid,
			destUsername: username,
			amount: amount
		};
		this.APIPOST("bank/transfer/create", obj, function(data) {
			success(data.message);
		}, function(data) {
			fail(data.message);
		});
	},

	createNumberTransfer: function(acid, amount, username, success, fail) {
		var obj = {
			accountid: acid,
			destNumber: username,
			amount: amount
		};
		this.APIPOST("bank/transfer/create", obj, function(data) {
			success(data.message);
		}, function(data) {
			fail(data.message);
		});
	},

	cancelTransfer: function(tid, success, fail) {
		var obj = {
			transferid: tid
		};
		this.APIPOST("bank/transfer/cancel", obj, function(data) {
			success(data.message);
		}, function(data) {
			fail(data.message);
		});
	},

	acceptTransfer: function(tid, success, fail) {
		var obj = {
			transferid: tid
		};
		this.APIPOST("bank/transfer/accept", obj, function(data) {
			success(data.message);
		}, function(data) {
			fail(data.message);
		});
	},

	addPhone: function(number, success, fail) {
		var obj = {
			number: number
		};
		this.APIPOST("carrier/phone/add", obj, function(data) {
			success(data.message);
		}, function(data) {
			fail(data.message);
		});
	},

	deletePhone: function(cid, success, fail) {
		var obj = {
			cellid: cid
		};
		this.APIPOST("carrier/phone/delete", obj, function(data) {
			success(data.message);
		}, function(data) {
			fail(data.message);
		});
	}
}

var Auth = new AuthAccess(localStorage.getItem("authcode"));

function getMoney(val)
{
	var cents = val % 100;
	var dollars = Math.floor(val / 100);
	var money = "$" + dollars + "." + cents;
	if(money.substr(money.length - 2) == '.0')
		money += '0';
	return money;
}