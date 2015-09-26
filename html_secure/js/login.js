//Login.js

function init()
{
	function submit() {
		var loginfo = {
			username: $("#username_field").val(),
			password: $("#password_field").val()
		}

		//Basic checks
		if(loginfo.username.length == 0) {
			return regFail("A username is required");
		}
		if(loginfo.password.length == 0) {
			return regFail("A password is required");
		}

		$.ajax({
			type: "POST",
			url: "../API/login",
			dataType: "json",
			data: loginfo,
			success: function(data) {
				if(data.status == "Success") {
					regSuccess(data);
				} else {
					regFail(data.message);
				}
			},
			failure: function() {
				regFail("Database error");
			}
		});
	}

	$(document).keypress(function(e) {
    	if(e.which == 13) {
        	submit();
    	}
	});
	$("#submit_button").click(submit);
}

function regFail(message)
{
	$("#message").html("Error: " + message + "!");
	$("#message").attr('style', 'color:red');
}

function regSuccess(data)
{
	localStorage.setItem("authcode", data.message);
	window.location.href = "index.html";
}