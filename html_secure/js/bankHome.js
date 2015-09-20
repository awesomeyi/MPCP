//bankHome.js

function getOpen(data, ttype)
{
	var ret = [];
	for(var i = 0; i < data.length; ++i) {
		if(data[i].complete == 0) {
			var tmp = data[i];
			tmp.transtype = ttype;
			ret.push(data[i]);
		}
	}
	return ret;
}

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
	Auth.getTransfers(function(transfers) {
		var open = getOpen(transfers.requested, "requested");
		open = open.concat(getOpen(transfers.received, "received"));
		if(open.length > 0) {
			$("#no_open_transfers").hide();
			$("#open_transfers").show();
		}
		for(var i = 0; i < open.length; ++i) {
			var row = document.createElement("tr");
			
			var user = document.createElement("td");
			var uns = open[i].username;
			if(open[i].transtype == "requested") {
				uns = "(To) " + uns;
			} else {
				uns = "(From) " + uns;
			}
			$(user).html(uns);
			$(row).append(user);
			
			var amount = document.createElement("td");
			$(amount).html(getMoney(open[i].amount));
			$(row).append(amount);
			
			var btd = document.createElement("td");
			var group = document.createElement("div");
			$(group).addClass("btn-group");

			var cancel = document.createElement("button");
			cancel.id = open[i].transferid;
			$(cancel).addClass("btn btn-danger");
			$(cancel).html("Cancel");

			$(cancel).click(function () {
				var tid = $(this).attr('id');
				Auth.cancelTransfer(tid, function () {
					window.location.reload();
				}, function () {
					window.location.reload();
				});
			});
			
			if (open[i].transtype == "received") {
				var accept = document.createElement("button");
				accept.id = open[i].transferid;
				$(accept).addClass("btn btn-success");
				$(accept).html("Accept");

				$(accept).click(function () {
					var tid = $(this).attr('id');
					Auth.acceptTransfer(tid, function () {
						window.location.reload();
					}, function () {
						window.location.reload();
					});
				});
				$(group).append(accept);
			}

			$(group).append(cancel);
			$(btd).append(group);
			$(row).append(btd);
			
			$("#open_transfers").append(row);
		}
	});
	$("#log_button").click(function() {
		Auth.logout();
	});
}