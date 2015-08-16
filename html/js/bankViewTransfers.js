//bankViewTransfers.js

function createRow(data)
{
	var row = document.createElement("tr");

	var start = document.createElement("td");
	$(start).html(data.starttime);
	$(row).append(start);

	var touser = document.createElement("td");
	var uns = data.username;
	if(data.complete > 0) {
		if(data.fromcheck > 0 && data.tocheck > 0) {
			uns += " (Accepted)";
			$(touser).attr('style', 'color:green');
		} else {
			uns += " (Rejected)";
			$(touser).attr('style', 'color:red');
		}
	} else {
		uns += " (Pending) ";
		$(touser).attr('style', 'color: blue');
	}
	$(touser).html(uns);
	$(row).append(touser);

	var amount = document.createElement("td");
	$(amount).html(getMoney(data.amount));
	$(row).append(amount);

	var end = document.createElement("td");
	$(end).html(data.endtime);
	$(row).append(end);

	return row;
}

function init()
{
	Auth.getTransfers(function(transfers) {

		//Process requested
		if(transfers.requested.length > 0) {
			$('#no_requested_transfers').hide();
			$("#requested_transfers").show();
		}
		var requested = transfers.requested;
		requested = requested.reverse();
		for(var i = 0; i < requested.length; ++i) {

			var row = createRow(requested[i]);

			if(requested[i].complete == 0) {
				var btd = document.createElement("td");
				var button = document.createElement("button");
				button.id = requested[i].transferid;
				$(button).addClass("btn btn-danger");
				$(button).html("Cancel");

				$(button).click(function() {
					var tid = $(this).attr('id');
					Auth.cancelTransfer(tid, function() {
						window.location.reload();
					}, function() {
						window.location.reload();
					});
				});

				$(btd).append(button);
				$(row).append(btd);
			} else {
				var r = document.createElement("td");
				$(row).append(r);
			}

			$("#requested_transfers").append(row);
		}

		//Process received
		if(transfers.received.length > 0) {
			$('#no_received_transfers').hide();
			$("#received_transfers").show();
		}
		var received = transfers.received;
		for(var i = 0; i < received.length; ++i) {

			var row = createRow(received[i]);

			if(received[i].complete == 0) {
				var btd = document.createElement("td");
				var group = document.createElement("div");
				$(group).addClass("btn-group");

				var cancel = document.createElement("button");
				cancel.id = received[i].transferid;
				$(cancel).addClass("btn btn-danger");
				$(cancel).html("Cancel");

				$(cancel).click(function() {
					var tid = $(this).attr('id');
					Auth.cancelTransfer(tid, function() {
						window.location.reload();
					}, function() {
						window.location.reload();
					});
				});

				var accept = document.createElement("button");
				accept.id = received[i].transferid;
				$(accept).addClass("btn btn-success");
				$(accept).html("Accept");

				$(accept).click(function() {
					var tid = $(this).attr('id');
					Auth.acceptTransfer(tid, function() {
						window.location.reload();
					}, function() {
						window.location.reload();
					});
				});

				$(group).append(cancel);
				$(group).append(accept);
				$(btd).append(group);
				$(row).append(btd);
			} else {
				var r = document.createElement("td");
				$(row).append(r);
			}

			$("#received_transfers").append(row);
		}
	});

	$("#log_button").click(function() {
		Auth.logout();
	});
}