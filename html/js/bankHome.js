//bankHome.js

function init()
{
	Auth.getUsername(function(un) {
		un = un.replace(un[0], un[0].toUpperCase());
		un += "!";
		$("#welcome_message").append(un);
	});
	$("#log_button").click(function() {
		Auth.logout();
	});
}