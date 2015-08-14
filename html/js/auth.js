//Auth.js

function AuthAccess(code)
{
	this.authcode = code;
	this.urlParams = "?authcode=" + this.authcode;
	this.verify();
}

AuthAccess.prototype = {
	constructor: AuthAccess,

	API: function(loc, success, fail) {
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

	verify: function() {
		this.API("verify", function() {}, function() {
			window.location.href = "login.html";
		});
	},

	logout: function() {
		this.API("logout", function() {
			window.location.href = "login.html";
		}, function() { });
	},

	getUsername: function(fun) {
		this.API("username", function(data) {
			fun(data.message);
		}, function () {});
	},

	getAccounts: function(fun) {
		this.API("bank/accounts", function(data) {
			fun(data.message);
		}, function () {});
	}
}

var Auth = new AuthAccess(localStorage.getItem("authcode"));