//bankCreateTransfer.js

function init()
{
	var allAccounts;
	var cselect = -1;

	Auth.getAccounts(function(acc) {
		allAccounts = acc;
		if(acc.length > 0) {
			$("#bank_accounts").empty();
		}
		for(var i = 0; i < acc.length; ++i) {

			var nele = document.createElement("li");
			nele.id = i;
			$(nele).html('<a href="#">' + acc[i].name + '</a>');
			$("#bank_accounts").append(nele);

			$(nele).click(function() {
				$("#dropdownMenu1").text($(this).text());
				$("#dropdownMenu1").val($(this).text());

				var id = $(this).attr('id');
				cselect = id;
				var money = getMoney(allAccounts[id].balance);

				$("#account_info").show();
				$("#account_info").html("<br>" + "Current balance: " + money);
			});
		}
	});
	$("#transfer_button").click(function() {
		var amount = $("#money_field").val();
		var un = $("#username_field").val();
		if(cselect == -1)
			return fail("Select a valid account");
		if(amount.length == 0)
			return fail("Enter an amount");
		if(un.length == 0)
			return fail("Enter a username");

		amount = Number(amount) * 100;
		Auth.createTransfer(allAccounts[cselect].accountid, amount, un, success, fail);
	});

	$("#log_button").click(function() {
		Auth.logout();
	});
}

function fail(message)
{
	$("#message").html("Error: " + message + "!");
	$("#message").attr('style', 'color:red');
}

function success(data)
{
	$("#message").html("Success!");
	$("#message").attr('style', 'color:green');
	window.location.href = "view_transfers.html"
}