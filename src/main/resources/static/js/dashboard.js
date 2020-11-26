function createRoomTableRow(room, isOwned) {
    let ownerColumn = isOwned ? "" : `<td>${room.ownerUser.name}</td>`;
    let dateStr = formatDate(new Date(room.dateOfLastUse));

    return `
    <tr id="${room.id}">
        <td class="l"><a href=/room/${room.id}>${room.name}</a></td>
        ${ownerColumn}
        <td>${room.language}</td>
        <td>${dateStr}</td>
        <td>${room.usersWithAccess.length ?? 0}</td>
        <td class="actions">${getRoomActions(room.id, isOwned)}</td>
    </tr>`
}

function getRoomActions(id, isOwned) {
    let result = "";
    if (isOwned) {
        result += `<button onclick="deleteRoom('${id}')">🟥</button>`;
        // TODO: show users action
        // TODO: change owner action
    }
    else {
        // TODO: leave room action
    }
    return result;
}

function deleteRoom(id) {
    $.ajax({
        url: "/api/v1/room/delete/" + id,
        type: 'DELETE',
        success: function (result) {
            if (result == "OK") {
                alert("room deleted");
                $("#" + id).remove();
            }
            else {
                alert("you dont have access");
            }
        }
    });
}

function getOwnedRooms() {
    $.ajax({
        url: "/api/v1/room/get-owned",
        type: 'GET',
        success: function (result) {
            if (Array.isArray(result) && result.length) {
                var resultJSON = eval(result);
                var content = "";

                resultJSON.forEach(function (room) {
                    content += createRoomTableRow(room, true);
                });

                $("#owned_rooms").html(content);
                $("#owned_rooms_parent").show();
            }
            else
                $("#owned_rooms_parent").hide();
        }
    });
}

function getAvailableRooms() {
    $.ajax({
        url: "/api/v1/room/get-available",
        type: 'GET',
        success: function (result) {
            if (Array.isArray(result) && result.length) {
                var resultJSON = eval(result);
                var content = "";

                resultJSON.forEach(function (room) {
                    content += createRoomTableRow(room, false);
                });

                $("#available_rooms").html(content);
                $("#available_rooms_parent").show();
            }
            else
                $("#available_rooms_parent").hide();
        }
    });
}

function logout() {
    let confirmed = confirm("logout");
    if (!confirmed)
        return;

    $.ajax({
        url: "/logout",
        type: 'POST',
        success: function (result) {
            alert("logged out successfully");
            location.href = "/";
        }
    });
}

function createRoom() {
    // TODO: client side validations
    $.ajax({
        url: "/api/v1/room/create",
        type: 'POST',
        data: { room_name: $("#room_name_input").val() },
        success: function (room) {
            console.log(room);
            alert("room created successfully");

            let content = createRoomTableRow(room, true) + $("#owned_rooms").html();

            $("#owned_rooms").html(content);
            $("#owned_rooms_parent").show();
        },
        error: function (jqXHR, status, error) {
            alert(jqXHR.responseText);
            console.log(jqXHR);
        }
    });
}

getAvailableRooms();
getOwnedRooms();