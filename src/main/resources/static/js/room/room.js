const SIDEPANEL_WIDTH_PX = 320;
const BUTTONS_WIDTH_PX = 72;

var sendButton = document.getElementById("send-msg-input");
sendButton.addEventListener("keypress", function(e)
{
	if(e.keyCode === 13)
	{
		e.preventDefault();
		document.getElementById("send-msg-btn").click();
	}
});

function closeNav() {
	$("#open_panel").show();
	$("#close_panel").hide();

	document.getElementById("sidepanel").style.width = "0";
	document.getElementById("content").style.right = BUTTONS_WIDTH_PX + "px";
}

function openNav()
{
    $("#open_panel").hide();
	$("#close_panel").show();

	document.getElementById("sidepanel").style.width = SIDEPANEL_WIDTH_PX + "px";
	document.getElementById("content").style.right = (SIDEPANEL_WIDTH_PX + BUTTONS_WIDTH_PX) + "px";
	
    updateMessages();
}
