//bankHome.js

function init()
{
	Auth.getUsername(function(un) {
		un = un[0].toUpperCase() + un.slice(1);
		un += "!";
		$("#welcome_message").append(un);
	});
	Auth.getAccounts(function(acc) {
		if(acc.length > 0) {
			$("#bank_accounts").empty();
		}
		for(var i = 0; i < acc.length; ++i) {
			var nele = document.createElement("li");
			nele.className = "list-group-item";
			nele.id = acc[i].accountid;
			$("#bank_accounts").append(nele);

			var bname = document.createElement("h4");
			$(bname).html(acc[i].name + " (" + acc[i].bankname + ")");
			$(nele).append(bname);

			var bal = document.createElement("p");
			var money = getMoney(acc[i].balance);
			$(bal).html(money);
			$(nele).append(bal);
		}
	});
	$("#log_button").click(function() {
		Auth.logout();
	});
}