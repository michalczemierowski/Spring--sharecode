function createChatMessage(id, userName, sendDateTime, content) {
	// show button only if user is owner
	let deleteButton = isOwner ? '<button class="top-right" onclick="deleteMessage(' + id + ')">&times;</button>' : "";
	return `
	<div class="msg" id="msg_${id}">
	  <p class="name">${userName}${deleteButton}</p>
	  <p class="content">${content}</p>
	  <p class="date">${sendDateTime}</p>
	</div>`
}

function formatDate(date)
{
	return date.getUTCFullYear() + "/" +
	("0" + (date.getUTCMonth() + 1)).slice(-2) + "/" +
	("0" + date.getUTCDate()).slice(-2) + " " +
	("0" + date.getUTCHours()).slice(-2) + ":" +
	("0" + date.getUTCMinutes()).slice(-2) + ":" +
	("0" + date.getUTCSeconds()).slice(-2);
}

function updateMessages() {
	$.ajax({
		url: "/api/v1/room/get/" + id + "/messages",
		type: 'GET',
		success: function (result) {
			//let jsonData = JSON.parse(result);
			//console.log(jsonData);
			console.log(result);

			let html = "";
			for (let i = 0; i < result.length; i++) {
				let row = result[i];

				let sendDate = new Date(row.sendDateTime);
				let dateStr = formatDate(sendDate);

				html += createChatMessage(row.id, row.user.name, dateStr, row.content);
			}

			$("#msg-div").html(html);
		}
	});
}

function sendMessage() {
	if (!$("#send-msg-input").val())
		return;

	$.ajax({
		url: "/api/v1/room/update/" + id + "/add-message",
		type: 'POST',
		data: { content: $("#send-msg-input").val() },
		success: function (result) {
			let sendDate = new Date(result.sendDateTime);
			let dateStr = formatDate(sendDate);

			let html = $("#msg-div").html();
			html += createChatMessage(result.id, result.user.name, dateStr, result.content);

			$("#msg-div").html(html);
		}
	});
	$("#send-msg-input").val("");
}

function deleteMessage(msg_id) {
	$.ajax({
		url: "/api/v1/room/update/" + id + "/delete-message",
		type: 'DELETE',
		data: { msg_id: msg_id },
		success: function (result) {
			console.log(typeof (msg_id));
			if (result == "OK") {
				$("#msg_" + msg_id).remove();
			}
		}
	});
}

function saveContent() {
	if (!isOwner)
		return;
	let path = "/api/v1/room/update/" + id + "/set-content";
	$.post(path, { content: editor.getValue() },
		function (data) {
			console.log(data);
		});
}

function deleteRoom() {
	$.ajax({
		url: "/api/v1/room/delete/" + id,
		type: 'DELETE',
		success: function (result) {
			if (result == "OK") {
				alert("room deleted");
				document.location.href = "/";
			}
			else {
				alert("you dont have access");
			}
		}
	});
}

function addAccess() {
	$.ajax({
		url: "/api/v1/room/update/" + id + "/add-access",
		type: 'POST',
		data: { target_user_id: "damianx2243@gmail.com" },
		success: function (result) {
			if (result == "OK") {
				alert("access added");
			}
			else {
				alert("you dont have access");
			}
		}
	});
}

function removeAccess() {
	$.ajax({
		url: "/api/v1/room/update/" + id + "/remove-access",
		type: 'POST',
		data: { target_user_id: "damianx2243@gmail.com" },
		success: function (result) {
			if (result == "OK") {
				alert("access removed");
			}
			else {
				alert("you dont have access");
			}
		}
	});
}