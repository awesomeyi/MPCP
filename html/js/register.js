//Register.js

function init()
{
	var uminlen = 5;
	var pminlen = 8;
	$("#submit_button").click(function() {
		var reginfo = {
			username: $("#username_field").val(),
			password: $("#password_field").val()
		}

		//Basic checks
		if(reginfo.username.length < uminlen) {
			return regFail("Username too short, must be over " + uminlen + " characters long");
		}
		if(reginfo.password.length < pminlen) {
			return regFail("Password too short, must be over " + pminlen + " characters long");
		}

		$.ajax({
			type: "POST",
			url: "API/register.php",
			dataType: "json",
			data: reginfo,
			success: function(data) {
				if(data.status == "Success") {
					regSuccess();
				} else {
					regFail(data.message);
				}
			},
			failure: function() {
				regFail("Database error");
			}
		});
	});
}

function regFail(message)
{
	$("#message").html("Error: " + message + "!");
	$("#message").attr('style', 'color:red');
}

function regSuccess()
{
	$("#message").html("Registration success! Redirecting you to the main page");
	$("#message").attr('style', 'color:green');
	setTimeout(function() {
		window.location="/";
	}, 2000);
}