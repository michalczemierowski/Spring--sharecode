function createRoomTableRow(room, isOwned) {
    let ownerColumn = isOwned ? "" : `<td>${room.ownerUser.name}</td>`;
    let dateStr = formatDate(new Date(room.dateOfLastUse));

    return `
    <tr id="${room.id}">
        <td class="l"><a href=/room/${room.id}>${room.name}</a></td>
        ${ownerColumn}
        <td>${langShortcutToName(room.language)}</td>
        <td>${dateStr}</td>
        <td>${room.usersWithAccess.length ?? 0}</td>
        <td class="actions">${getRoomActions(room.id, isOwned)}</td>
    </tr>`
}

function langShortcutToName(shortcut) {
    switch (shortcut) {
        case "assembly_x86":
            return "Assembly X86";
        case "batchfile":
            return "Batch";
        case "c_cpp":
            return "C/C++";
        case "csharp":
            return "C#";
        case "css":
            return "CSS";
        case "django":
            return "Django";
        case "dockerfile":
            return "Docker";
        case "elixir":
            return "Elixir";
        case "fsharp":
            return "F#";
        case "glsl":
            return "GLSL";
        case "golang":
            return "Go";
        case "html":
            return "HTML";
        case "html_elixir":
            return "HTML Elixir";
        case "html_ruby":
            return "HTML Ruby";
        case "java":
            return "Java";
        case "javascript":
            return "Javascript";
        case "json":
            return "JSON";
        case "julia":
            return "Julia";
        case "kotlin":
            return "Kotlin";
        case "lua":
            return "Lua";
        case "markdown":
            return "Markdown";
        case "matlab":
            return "Matlab";
        case "objectivec":
            return "Objective C";
        case "pgsql":
            return "pgSQL";
        case "php":
            return "PHP";
        case "plain_text":
            return "Plain Text";
        case "powershell":
            return "Powershell";
        case "python":
            return "Python";
        case "ruby":
            return "Ruby";
        case "rust":
            return "Rust";
        case "scala":
            return "Scala";
        case "sh":
            return "SH";
        case "sql":
            return "SQL";
        case "sqlserver":
            return "SQL Server";
        case "swift":
            return "Swift";
        case "typescript":
            return "Type Script";
        case "xml":
            return "XML";
        case "yaml":
            return "YAML";
    }
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
        type: "DELETE",
        success: function (response) {
            if (response == "OK") {
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
        type: "GET",
        success: function (response) {
            if (Array.isArray(response) && response.length) {
                var resultJSON = eval(response);
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
        type: "GET",
        success: function (response) {
            if (Array.isArray(response) && response.length) {
                var resultJSON = eval(response);
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
        type: "POST",
        success: function (response) {
            alert("logged out successfully");
            location.href = "/";
        }
    });
}

function createRoom() {
    // TODO: client side validations
    $.ajax({
        url: "/api/v1/room/create",
        type: "POST",
        data: {
            room_name: $("#room_name_input").val(),
            language: $("#language_input").val()
        },
        success: function (response) {
            console.log(response);
            alert("room created successfully");

            let content = createRoomTableRow(response, true) + $("#owned_rooms").html();

            $("#owned_rooms").html(content);
            $("#owned_rooms_parent").show();
        },
        error: function (jqXHR, status, error) {
            alert(jqXHR.responseText);
            console.log(jqXHR);
        }
    });
}

function changeUserName() {
    let userNameId = "user_name"
    let userNameInputVal = $("#user_name_input").val();

    $.ajax({
        url: "/api/v1/user/set-name",
        type: "POST",
        data: { name: userNameInputVal },
        success: function (response) {
            console.log(response);
            alert("user name changed successfully to " + response);
            $("#" + userNameId).html(response);
        },
        error: function (jqXHR, status, error) {
            alert(jqXHR.responseText);
            console.log(jqXHR);
        }
    });
}

getAvailableRooms();
getOwnedRooms();