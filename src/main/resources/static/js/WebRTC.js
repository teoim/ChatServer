
// window.addEventListener("DOMContentLoaded", main);   // triggeres after dom elements are loaded
window.addEventListener("load", main);  // triggered after dom elements AND images, stylesheets, fonts etc are loaded

var callIcon, videoCallIcon, inCallIcon, inVideoCallIcon;

function main(){

    console.log("Loading WebRTC module...");

    callIcon = document.getElementById("callIcon");
    videoCallIcon = document.getElementById("videoCallIcon");
    inCallIcon = document.getElementById("inCallIcon");
    inVideoCallIcon = document.getElementById("inVideoCallIcon");

    callIcon.addEventListener("click", startCall);
    videoCallIcon.addEventListener("click", startVideoCall);
    inCallIcon.addEventListener("click", endCall);
    inVideoCallIcon.addEventListener("click", endVideoCall);


    console.log("WebRTC module loaded.");

}

function startCall(){
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("inCallIcon").style.display = "block";
    document.getElementById("inVideoCallIcon").style.display = "none";
}

function startVideoCall(){
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("inCallIcon").style.display = "none";
    document.getElementById("inVideoCallIcon").style.display = "block";
}

function endCall(){
    document.getElementById("videoBox").style.display = "none";
    document.getElementById("iconsDiv").style.display = "flow";
}

function endVideoCall(){
    document.getElementById("videoBox").style.display = "none";
    document.getElementById("iconsDiv").style.display = "flow";
}
