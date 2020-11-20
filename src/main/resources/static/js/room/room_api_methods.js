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
				html += `
            <div class="msg" id="msg_${row.id}">
              ${row.user.name}<button onclick="deleteMessage(${row.id})">x</button>
              <p class="msgcontent">${row.content}</p>
            </div>`;
			}

			$("#messages").html(html);
		}
	});
}

function sendMessage() {
	if (!$("#msgcontent").val())
		return;

	$.ajax({
		url: "/api/v1/room/update/" + id + "/add-message",
		type: 'POST',
		data: { content: $("#msgcontent").val() },
		success: function (result) {
			console.log(result);
			let html = $("#messages").html();
			html += `
          <div class="msg" id="msg_${result.id}">
            ${result.user.name}<button onclick="deleteMessage(${result.id})">x</button>
            <p class="msgcontent">${result.content}</p>
          </div>`;
			$("#messages").html(html);
		}
	});
	$("#msgcontent").val("");
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