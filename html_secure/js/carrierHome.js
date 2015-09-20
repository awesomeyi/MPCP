//carrierHome.js

function init()
{
	Auth.getUsername(function(un) {
		un = un[0].toUpperCase() + un.slice(1);
		un += "!";
		$("#welcome_message").append(un);
	});

	Auth.getPhones(function(phones) {
		if(phones.length > 0) {
			$("#no_phones").hide();
			$("#all_phones").show();
		}
		for(var i = 0; i < phones.length; ++i) {
			var row = document.createElement("tr");

			var number = document.createElement("td");
			var ufn = phones[i].cellnumber;
			var fn = "(";
			fn += ufn.substr(0, 3);
			fn += ") ";
			fn += ufn.substr(3, 3);
			fn += "-";
			fn += ufn.substr(6, 4);
			$(number).html(fn);

			var button = document.createElement("td");
			var remove = document.createElement("button");
			remove.id = phones[i].cellid;
			$(remove).click(function() {
				var cid = $(this).attr("id");	
				Auth.deletePhone(cid, function() {
					window.location.reload();
				}, function() {
					window.location.reload();
				});
			});

			$(remove).addClass("btn btn-danger");
			$(remove).html("Delete");
			$(button).append(remove);

			$(row).append(number);
			$(row).append(button);

			$(row).insertBefore("#add_phone_row");
		}

	});
	var show = false;

	$("#add_phone").click(function() {
		if(show) {
			$("#add_phone_row").hide();
			show = false;
		} else {
			$("#add_phone_row").show();
			show = true;
		}
	});

	$("#add_button").click(function() {
		var number = $("#add_number").val();
		Auth.addPhone(number, function() {
			window.location.reload();
			show = false;
			$("#add_phone_row").hide();
		}, function(message) {
			$("#message").html(message);
			$("#message").attr('style', 'color:red');
		});
	});
	
	$("#log_button").click(function() {
		Auth.logout();
	});
}