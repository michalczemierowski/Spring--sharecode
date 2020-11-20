function closeNav() {
	$("#open_panel").show();
	$("#close_panel").hide();
	document.getElementById("sidepanel").style.width = "0";
}

function openNav()
{
    $("#open_panel").hide();
	$("#close_panel").show();
	document.getElementById("sidepanel").style.width = "320px";
    updateMessages();
}