//checkAuth

var authcode = localStorage.getItem("authcode");

$.ajax({
	url: "../API/verify.php?authcode=" + authcode,
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

function success()
{
	//Success!
}
function fail()
{
	window.location.href = "login.html";
}