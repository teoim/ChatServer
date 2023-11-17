
// window.addEventListener("DOMContentLoaded", main);   // triggeres after dom elements are loaded
window.addEventListener("load", main);  // triggered after dom elements AND images, stylesheets, fonts etc are loaded

var callIcon, videoCallIcon, inCallIcon, inVideoCallIcon;
var targetUsername, myUsername;

var myPeerConnection

var ws;
var stompClient;
var subscription001;


const mediaConstraints = {
    audio: true, // Audio track on / off
    video: true, // Video track on / off
};

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

    // Connect and listen to the ICE server

    ws = new SockJS("/api/webrtc");   // unlike WebSocket(), SockJS() provides fall-back protocols, if WebSockets are not supported in the browser

    stompClient = Stomp.over(ws);

    // Custom subscription ID, passed to stompClient.subscribe ( if it's not passed, stomp automatically creates an ID )
    var myICEsubId001 = 'my-ICE-subscription-id-001';

    // Prepare Stomp.connect() parameters - stompCallbackFunction and stompConnectError:
    stompCallbackFunction = function(frame){
        subscription001 = stompClient.subscribe("/user/queue/sendICEMessage", function(message){     //    "/topic/generalChat" is a broker destination
                console.log("stompClient received general message: " + message.body);

                let msg = JSON.parse(message.body);
                console.log("Received ICE message: " + msg);

            }
            , {id : myICEsubId001});

    }

    console.log("WebRTC module loaded.");

}

function startCall(event){
    console.log("WebRTC.startCall()");
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("inCallIcon").style.display = "block";
    document.getElementById("inVideoCallIcon").style.display = "none";

    mediaConstraints.audio = true;
    mediaConstraints.video = false;
    invite(event);
}

function startVideoCall(event){
    console.log("WebRTC.startVideoCall()");
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("inCallIcon").style.display = "none";
    document.getElementById("inVideoCallIcon").style.display = "block";

    mediaConstraints.audio = true;
    mediaConstraints.video = true;
    invite(event);
}

function endCall(){
    console.log("WebRTC.endCall()");
    document.getElementById("videoBox").style.display = "none";
    document.getElementById("iconsDiv").style.display = "flow";
    hangUpCall();
}

function endVideoCall(){
    console.log("WebRTC.endVideoCall()");
    document.getElementById("videoBox").style.display = "none";
    document.getElementById("iconsDiv").style.display = "flow";
    hangUpCall();
}


async function sendMessageToServer(message){
    // console.log("WebRTC.sendMessageToServer() - message: " + message);
    let msg = JSON.stringify(message);
    console.log("WebRTC.sendMessageToServer() - message: " + msg);


    // TODO create a controller method(s) for the signalling server and build the connection object (jQuery?)
    // something like $.get( toUserTextMessagesFinalURL, function(data, status){ ... }
    // connection.send(msg);

    var token_value = "";
    var token_name = "";
    // Used when csrf is enabled
    if(document.getElementsByName("_csrf").length > 0){
        var token_value = document.getElementsByName("_csrf")[0].value;
        var token_name = document.getElementsByName("_csrf")[0].name;
        // console.log( "CSRF token name: " + token_name + " value: " + token_value);
    } else {
        console.log("Csrf may be disabled - check system configuration - csrf token not found.");
    }

    console.log( "CSRF token name: " + token_name + " value: " + token_value);

    try {
        const response = await fetch("http://localhost:8080/api/webrtc/ice-server/message", {
            method: "POST",
            headers: {
                credentials: "include",
                // mode: "no-cors",
                "Content-Type": "application/json; charset=UTF-8",
                token_name: token_value,
            },
            body: JSON.stringify(message),
        });

        // const result = await response.json();
        const result = await response;  // TODO: change to hson after finishing the controller method
        console.log("Success:", result);
    } catch (error) {
        console.error("Error:", error);
    }

    /*$.post("http://localhost:8080/api/webrtc/ice-server/message", JSON.stringify(message), (data, status) => {
        console.log(data);
    });*/
}


function invite(event) {
    // console.log("WebRTC.invite() - event: " + event.type);  // click
    // console.log("Inviting " + event.target + " for a call");    // [object HTMLImageElement]

    if (myPeerConnection) {
        alert("A call is already in progress...");
    } else {
        const clickedUsername = document.getElementById("iAmChattingWith").value;
        console.log("Inviting " + clickedUsername + " for a call");

        if (clickedUsername === myUsername) {
            alert(
                "You could talk to yourself but that would be weird.",
            );
            return;
        }

        targetUsername = clickedUsername;
        createPeerConnection();

        navigator.mediaDevices
            .getUserMedia(mediaConstraints)
            .then((localStream) => {
                document.getElementById("localVideo").srcObject = localStream;
                localStream
                    .getTracks()
                    .forEach((track) => myPeerConnection.addTrack(track, localStream));
            })
            .catch(handleGetUserMediaError);
    }
}


function handleGetUserMediaError(e) {
    console.log("WebRTC.handleGetUserMediaError()");
    switch (e.name) {
        case "NotFoundError":
            alert(
                "Unable to open your call because no camera and/or microphone" +
                "were found.",
            );
            break;
        case "SecurityError":
        case "PermissionDeniedError":
            // Do nothing; this is the same as the user canceling the call.
            break;
        default:
            alert(`Error opening your camera and/or microphone: ${e.message}`);
            break;
    }

    closeVideoCall();
}


function createPeerConnection() {
    console.log("WebRTC.createPeerConnection()");
    myPeerConnection = new RTCPeerConnection({
        iceServers: [
            // Information about ICE servers (STUN and/or TURN servers) - TODO: should create and use my own ICE servers
            {
                // Known public STUN server:
                urls: "stun:stun.stunprotocol.org",
            },
        ],
    });

    // The first 3 event handlers are required:
    myPeerConnection.onicecandidate = handleICECandidateEvent;
    myPeerConnection.ontrack = handleTrackEvent;
    myPeerConnection.onnegotiationneeded = handleNegotiationNeededEvent;

    // These other event handlers are not required but are useful:
    // (plus there's other event handlers we can set)
    myPeerConnection.onremovetrack = handleRemoveTrackEvent;
    myPeerConnection.oniceconnectionstatechange = handleICEConnectionStateChangeEvent;
    myPeerConnection.onicegatheringstatechange = handleICEGatheringStateChangeEvent;
    myPeerConnection.onsignalingstatechange = handleSignalingStateChangeEvent;
}


function handleNegotiationNeededEvent() {
    console.log("WebRTC.handleNegotiationNeededEvent()");
    myPeerConnection
        .createOffer()
        .then((offer) => myPeerConnection.setLocalDescription(offer))
        .then(() => {
            sendMessageToServer({
                name: myUsername,
                target: targetUsername,
                type: "video-offer",
                sdp: myPeerConnection.localDescription
            });
        })
        .catch(reportError);
}


// We receive a video call from someone
function handleVideoOfferMsg(msg) {
    console.log("WebRTC.handleVideoOfferMsg() - msg: " + msg);
    let localStream = null;

    targetUsername = msg.name;
    createPeerConnection(); // Create and configure a new RTCPeerConnection

    const desc = new RTCSessionDescription(msg.sdp);    // The caller's session description

    myPeerConnection
        .setRemoteDescription(desc)
        .then(() => navigator.mediaDevices.getUserMedia(mediaConstraints))
        .then((stream) => {
            localStream = stream;
            document.getElementById("local_video").srcObject = localStream;

            localStream
                .getTracks()
                .forEach((track) => myPeerConnection.addTrack(track, localStream));
        })
        .then(() => myPeerConnection.createAnswer())
        .then((answer) => myPeerConnection.setLocalDescription(answer))
        .then(() => {
            const msg = {
                name: myUsername,
                target: targetUsername,
                type: "video-answer",
                sdp: myPeerConnection.localDescription
            };

            sendMessageToServer(msg);
        })
        .catch(handleGetUserMediaError);
}


// SENDING ice candidates:
// When we receive an ice candidate from the browser, we send it to the server for the other peer.
function handleICECandidateEvent(event) {
    console.log("WebRTC.handleICECandidateEvent() - event: " + event);
    if (event.candidate) {
        sendMessageToServer({
            type: "new-ice-candidate",
            target: targetUsername,
            candidate: event.candidate
        });
    }
}


// RECEIVEING ice candidates:
// The handleNewICECandidateMsg() function is called by our main WebSocket incoming message code.
function handleNewICECandidateMsg(msg) {
    console.log("WebRTC.handleNewICECandidateMsg() - msg: " + msg);
    // Build ice candidate object
    const candidate = new RTCIceCandidate(msg.candidate);

    // Deliver candidate to ICE layer
    myPeerConnection.addIceCandidate(candidate).catch(reportError);
}


// RECEIVING new streams
function handleTrackEvent(event) {
    console.log("WebRTC.handleTrackEvent() - event: " + event);
    // TODO: check track type (audio/video/etc)

    // Add video track to html element
    document.getElementById("receivedVideo").srcObject = event.streams[0];
    //document.getElementById("hangup-button").disabled = false;
}


// REMOVE streams
function handleRemoveTrackEvent(event) {
    console.log("WebRTC.handleRemoveTrackEvent() - event: " + event);
    const stream = document.getElementById("receivedVideo").srcObject;
    const trackList = stream.getTracks();

    if (trackList.length === 0) {
        closeVideoCall();
    }
}


// HANGING up
function hangUpCall() {
    console.log("WebRTC.hangUpCall()");
    closeVideoCall();
    sendMessageToServer({
        name: myUsername,
        target: targetUsername,
        type: "hang-up",
    });
}


// ENDING the call
function closeVideoCall() {
    console.log("WebRTC.closeVideoCall()");
    const remoteVideo = document.getElementById("receivedVideo");
    const localVideo = document.getElementById("localVideo");

    if (myPeerConnection) {
        myPeerConnection.ontrack = null;
        myPeerConnection.onremovetrack = null;
        myPeerConnection.onremovestream = null;
        myPeerConnection.onicecandidate = null;
        myPeerConnection.oniceconnectionstatechange = null;
        myPeerConnection.onsignalingstatechange = null;
        myPeerConnection.onicegatheringstatechange = null;
        myPeerConnection.onnegotiationneeded = null;

        if (remoteVideo.srcObject) {
            remoteVideo.srcObject.getTracks().forEach((track) => track.stop());
        }

        if (localVideo.srcObject) {
            localVideo.srcObject.getTracks().forEach((track) => track.stop());
        }

        myPeerConnection.close();
        myPeerConnection = null;
    }

    remoteVideo.removeAttribute("src");
    remoteVideo.removeAttribute("srcObject");
    localVideo.removeAttribute("src");
    remoteVideo.removeAttribute("srcObject");

    // document.getElementById("hangup-button").disabled = true;
    targetUsername = null;
}


// ICE CONNECTION STATE CHANGE
// call terminated from other side, etc
function handleICEConnectionStateChangeEvent(event) {
    console.log("WebRTC.handleICEConnectionStateChangeEvent() - event: " + event);
    switch (myPeerConnection.iceConnectionState) {
        case "closed":
        case "failed":
            closeVideoCall();
            break;
    }
}


// ICE SIGNALING STATE
function handleSignalingStateChangeEvent(event) {
    console.log("WebRTC.handleSignalingStateChangeEvent() - event: " + event);
    switch (myPeerConnection.signalingState) {
        // Deprecated (replaced by closed iceConnectionState) - we watch for it only for backwards compatibility
        case "closed":
            closeVideoCall();
            break;
    }
}


// ICE GATHERING STATE
// usefull for debugging or to detect when ice candidate gathering has finished
function handleICEGatheringStateChangeEvent(event) {
    console.log("WebRTC.handleICEGatheringStateChangeEvent() - event: " + event);
}
