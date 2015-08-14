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
					success();
				} else {
					fail();
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
	getUsername: function() {
		this.API("")
	}
}

var Auth = new AuthAccess(localStorage.getItem("authcode"));