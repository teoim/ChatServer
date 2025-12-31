
// window.addEventListener("DOMContentLoaded", main);   // triggeres after dom elements are loaded
window.addEventListener("load", main);  // triggered after dom elements AND images, stylesheets, fonts etc are loaded

// UI Elements
var callIcon, videoCallIcon, innerCallIcon, innerVideoCallIcon;
var innerCameraOnIcon, innerCameraOffIcon, innerMicOnIcon, innerMicOffIcon;
var targetUsername, myUsername;

// RTCPeerConnection
var myPeerConnection;
var myLocalStream, myRemoteStream;

// WebSocket & STOMP
var wskt;
var webRtcStompClient;
var webRtcSubscription001;

// RTCPeerConnection media constraints
const mediaConstraints = {
    audio: false, // Audio track on / off
    video: false, // Video track on / off
};


function main(){

    console.debug("WebRTC.main()\nLoading WebRTC module...");

    myUsername = document.getElementById("emailSpan").textContent;

    callIcon = document.getElementById("callIcon");
    videoCallIcon = document.getElementById("videoCallIcon");
    innerCallIcon = document.getElementById("innerCallIcon");
    innerVideoCallIcon = document.getElementById("innerVideoCallIcon");
    innerCameraOnIcon = document.getElementById("innerCameraOnIcon");
    innerCameraOffIcon = document.getElementById("innerCameraOffIcon");
    innerMicOnIcon = document.getElementById("innerMicOnIcon");
    innerMicOffIcon = document.getElementById("innerMicOffIcon");

    callIcon.addEventListener("click", startCall);
    videoCallIcon.addEventListener("click", startVideoCall);
    innerCallIcon.addEventListener("click", endCall);
    innerVideoCallIcon.addEventListener("click", endVideoCall);
    innerCameraOnIcon.addEventListener("click", turnCameraOff);
    innerCameraOffIcon.addEventListener("click", turnCameraOn);
    innerMicOnIcon.addEventListener("click", turnMicrophoneOff);
    innerMicOffIcon.addEventListener("click", turnMicrophoneOn);

    // Connect and listen to the ICE server

    wskt = new SockJS("/sendICEMessage");   // unlike WebSocket(), SockJS() provides fall-back protocols, if WebSockets are not supported in the browser

    webRtcStompClient = Stomp.over(wskt);

    // Custom subscription ID, passed to webRtcStompClient.subscribe ( if it's not passed, stomp automatically creates an ID )
    var myICEsubId001 = 'my-ICE-subscription-id-001';

    // Prepare Stomp.connect() parameters - rtcStompCallbackFunction and rtcStompConnectError:
    rtcStompCallbackFunction = function(frame){
        console.debug("WebRTC.js rtcStompCallbackFunction");
        webRtcSubscription001 = webRtcStompClient.subscribe("/user/queue/sendICEMessage", function(message){     //    "/user/queue/sendICEMessage" is a broker destination

                let parsedMsgBody = JSON.parse(message.body);

                switch (parsedMsgBody.type) {
                    case "new-ice-candidate":
                        console.info("Received ice candidate...");
                        handleNewICECandidateMsg(parsedMsgBody);
                        break;
                    case "video-offer":
                        console.info("Received video offer...");
                        handleVideoOfferMsg(parsedMsgBody);
                        break;
                    case "video-answer":
                        console.info("Received video answer...");
                        handleVideoAnswerMsg(parsedMsgBody);
                        break;
                    case "hang-up":
                        console.info("Received video answer...");
                        console.warn("TODO hang-up: double check flow for endVideoCall().");
                        endVideoCall()
                        break;
                    default:
                        console.warn("parsedMsgBody.type not treated: ", parsedMsgBody.type);
                        break;
                }

            }
            , {id : myICEsubId001});

    }

    rtcStompConnectError = function(err){
        console.error("WebRTC.js - STOMP  connection error: \n", err);
    }

    webRtcStompClient.connect( {}, rtcStompCallbackFunction, rtcStompConnectError);

    console.debug("WebRTC module loaded.");

}

function startCall(event){
    console.debug("WebRTC.startCall()");
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("innerCallIcon").style.display = "";

    document.getElementById("innerVideoCallIcon").style.display = "none";
    document.getElementById("innerVideoCallDiv").style.display = "none";

    document.getElementById("innerCameraOnIcon").style.display = "none";
    document.getElementById("innerCameraOnDiv").style.display = "none";

    document.getElementById("innerMicOffIcon").style.display = "none";
    document.getElementById("innerMicOffDiv").style.display = "none";

    mediaConstraints.audio = true;
    mediaConstraints.video = false;
    invite(event);
}

function startVideoCall(event,answer){
    console.debug("WebRTC.startVideoCall()");
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("innerCallIcon").style.display = "none";
    document.getElementById("innerCallDiv").style.display = "none";
    document.getElementById("innerVideoCallIcon").style.display = "";

    document.getElementById("innerCameraOffIcon").style.display = "none";
    document.getElementById("innerCameraOffDiv").style.display = "none";

    document.getElementById("innerMicOffIcon").style.display = "none";
    document.getElementById("innerMicOffDiv").style.display = "none";

    mediaConstraints.audio = true;
    mediaConstraints.video = true;

    // Only invite() when startVideoCall was triggered by a click.
    if( (event != null) || (answer != true) ) {
        invite(event);
    } else {
        console.warn( "---- event null and answer true ----" );
    }
}

function initAnswerToVideoCall(){
    console.debug("WebRTC.initAnswerToVideoCall()");
    document.getElementById("videoBox").style.display = "flow";
    document.getElementById("iconsDiv").style.display = "none";
    document.getElementById("innerCallIcon").style.display = "none";
    document.getElementById("innerCallDiv").style.display = "none";
    document.getElementById("innerVideoCallIcon").style.display = "block";

    mediaConstraints.audio = true;
    mediaConstraints.video = true;

}

function endCall(){
    console.debug("WebRTC.endCall()");
    document.getElementById("videoBox").style.display = "none";
    document.getElementById("iconsDiv").style.display = "flow";
    resetInCallIconsAndDivs();
    hangUpCall();
}

function endVideoCall(){
    console.debug("WebRTC.endVideoCall()");
    document.getElementById("videoBox").style.display = "none";
    document.getElementById("iconsDiv").style.display = "flow";
    resetInCallIconsAndDivs();
    hangUpCall();
}

function resetInCallIconsAndDivs(){
    document.getElementById("innerCallIcon").style.display = "";
    document.getElementById("innerCallDiv").style.display = "";

    document.getElementById("innerVideoCallIcon").style.display = "";
    document.getElementById("innerVideoCallDiv").style.display = "";

    document.getElementById("innerCameraOnIcon").style.display = "";
    document.getElementById("innerCameraOnDiv").style.display = "";
    document.getElementById("innerCameraOffIcon").style.display = "";
    document.getElementById("innerCameraOffDiv").style.display = "";

    document.getElementById("innerMicOnIcon").style.display = "";
    document.getElementById("innerMicOnDiv").style.display = "";
    document.getElementById("innerMicOffIcon").style.display = "";
    document.getElementById("innerMicOffDiv").style.display = "";
}

function turnCameraOff(){
    document.getElementById("innerCameraOnDiv").style.display = "none";
    document.getElementById("innerCameraOffDiv").style.display = "";
    innerCameraOnIcon.style.display = "none";
    innerCameraOffIcon.style.display = "";
    myLocalStream.getTracks()
        .forEach((track) => {
            if(track.kind === "video"){
                console.log("Camera enabled - turning off.");
                track.enabled = false;
            }
        });
}

function turnCameraOn(){
    document.getElementById("innerCameraOnDiv").style.display = "";
    document.getElementById("innerCameraOffDiv").style.display = "none";
    innerCameraOnIcon.style.display = "";
    innerCameraOffIcon.style.display = "none";
    myLocalStream.getTracks()
        .forEach((track) => {
            if(track.kind === "video"){
                console.log("Camera disabled - turning on.");
                track.enabled = true;
            }
        });
}

function turnMicrophoneOff(){
    document.getElementById("innerMicOnDiv").style.display = "none";
    document.getElementById("innerMicOffDiv").style.display = "";
    innerMicOnIcon.style.display = "none";
    innerMicOffIcon.style.display = "";
    myLocalStream.getTracks()
        .forEach((track) => {
            if(track.kind === "audio"){
                console.log("Microphone enabled - turning off.");
                track.enabled = false;
            }
        });
}

function turnMicrophoneOn(){
    document.getElementById("innerMicOnDiv").style.display = "";
    document.getElementById("innerMicOffDiv").style.display = "none";
    innerMicOnIcon.style.display = "";
    innerMicOffIcon.style.display = "none";
    myLocalStream.getTracks()
        .forEach((track) => {
            if(track.kind === "audio"){
                console.log("Microphone disabled - turning on.");
                track.enabled = true;
            }
        });
}


async function sendMessageToServer(message){
    // console.debug("WebRTC.sendMessageToServer() - message: ", message);
    let msg = JSON.stringify(message);
    console.debug("WebRTC.sendMessageToServer() - message: ", msg);

    var token_value = "";
    var token_name = "";
    // Used when csrf is enabled
    if(document.getElementsByName("_csrf").length > 0){
        var token_value = document.getElementsByName("_csrf")[0].value;
        var token_name = document.getElementsByName("_csrf")[0].name;
        //console.log( "CSRF token name: " + token_name + " value: " + token_value);
    } else {
//        console.log("Csrf may be disabled - check system configuration - csrf token not found.");
//        console.warn("Csrf token not found.");
    }

    try {
        //const response = await fetch("http://localhost:8080/api/webrtc/ice-server/message", {
        const response = await fetch("https://cchat.ddns.net/api/webrtc/ice-server/message", {
            method: "POST",
            headers: {
                credentials: "include",
                "Content-Type": "application/json; charset=UTF-8",
                token_name: token_value,
            },
//            body: message,
            body: msg,
        });

        const result = await response;  // TODO: change to hson after finishing the controller method

        // result: { type, url, status, redirected, ok, statusText, headers, body, bodyUsed }
        console.info("POST ", result.status, result.url);

    } catch (error) {
        console.error("Error while sending message to server:", error);
    }

}


function invite(event) {
    console.debug("WebRTC.invite()");

    if (myPeerConnection) {
        console.warn("A call is already in progress...");
    } else {
        targetUsername = sessionStorage.getItem("iAmChattingWith");
        console.info("Inviting " + targetUsername + " for a call");

        if (targetUsername === myUsername) {
            console.warn("You don't want to talk to yourself..");
            return;
        }

        createPeerConnection();

        navigator.mediaDevices
            .getUserMedia(mediaConstraints)
            .then((localStream) => {
                myLocalStream = localStream;
                document.getElementById("smallScreen").srcObject = localStream;
                localStream
                    .getTracks()
                    .forEach((track) => myPeerConnection.addTrack(track, localStream)); // triggers negotiation needed event - handleNegotiationNeededEvent()
            })
            .catch(handleGetUserMediaError);
    }
}


/**
* Initialize myPeerConnection with basic configuration
*/
function createPeerConnection() {
    console.debug("WebRTC.createPeerConnection()");

    if (myPeerConnection) {
        console.warn("A call is already in progress!");
        return;
    }
    console.info("Creating new peer connection.");

    myPeerConnection = new RTCPeerConnection({
        iceServers: [
            // Information about ICE servers (STUN and/or TURN servers) - TODO: should create and use my own ICE servers maybe
            {
                // Known public STUN server:
                urls: "stun:stun.stunprotocol.org"
            }
            ,{ urls: "stun:stun4.l.google.com:19302" }
            ,{ urls: "stun:stun4.l.google.com:5349" }
        ],
    });

    // The first 3 event handlers are required:
    myPeerConnection.onicecandidate = handleICECandidateEvent;
    myPeerConnection.ontrack = ({ track, streams }) => handleTrackEvent(track, streams);
    myPeerConnection.onnegotiationneeded = handleNegotiationNeededEvent;

    // These other event handlers are not required but are useful:
    // (plus there's other event handlers we can set)
    //myPeerConnection.onremovetrack = handleRemoveTrackEvent;
    //myPeerConnection.oniceconnectionstatechange = handleICEConnectionStateChangeEvent;
    //myPeerConnection.onicegatheringstatechange = handleICEGatheringStateChangeEvent;
    //myPeerConnection.onsignalingstatechange = handleSignalingStateChangeEvent;
}


function handleNegotiationNeededEvent(event) {
    console.debug("WebRTC.handleNegotiationNeededEvent()");
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


// We receive a video call from someone [seemsOK]
function handleVideoOfferMsg(msg) {
    console.debug("WebRTC.handleVideoOfferMsg() - msg: ", msg);
    //startVideoCall(null,true);   //TODO bug? - sends a video-offer message at the end.. we just need the UI elements behavior
    initAnswerToVideoCall();    // only UI elements and mediaConstraints

    targetUsername = msg.name;
    createPeerConnection(); // Create and configure a new RTCPeerConnection, if non existent.

    const desc = new RTCSessionDescription(msg.sdp);    // The caller's session description

    myPeerConnection
        .setRemoteDescription(desc)
        .then(() => navigator.mediaDevices.getUserMedia(mediaConstraints))
        .then((localStream) => {
            myLocalStream = localStream;
            document.getElementById("smallScreen").srcObject = myLocalStream;

            myLocalStream
                .getTracks()
                .forEach((track) => myPeerConnection.addTrack(track, myLocalStream));   // addTrack() triggers negotiationNeededEvent
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

// We received an answer to our video call
function handleVideoAnswerMsg(msg) {
    console.debug("WebRTC.handleVideoAnswerMsg()");
    const desc = new RTCSessionDescription(msg.sdp);
    myPeerConnection.setRemoteDescription(desc)
/*
        .then(() => navigator.mediaDevices.getUserMedia(mediaConstraints))
            .then((stream) => {
                localStream = stream;
                console.warn("localStream: ", localStream);
                console.warn("msg: ", msg);
                document.getElementById("smallScreen").srcObject = localStream;

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
*/
    //.catch(window.reportError);
}


// SENDING ice candidates:
// When we receive an ice candidate from the browser, we send it to the server for the other peer.
function handleICECandidateEvent(event) {
    console.debug("WebRTC.handleICECandidateEvent()");
//    console.log("WebRTC.handleICECandidateEvent() - event: \n", event);
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
    console.debug("WebRTC.handleNewICECandidateMsg()");

    let msgCandidate = msg.candidate;

    // Build ice candidate object
    const candidate = new RTCIceCandidate(msgCandidate);

    createPeerConnection();     // TODO double check whole function

    // Deliver candidate to ICE layer
    myPeerConnection.addIceCandidate(candidate).catch(reportError);
}


// RECEIVING new streams
/*function handleTrackEvent(event) {
    console.debug("WebRTC.handleTrackEvent(event)");

    // TODO: check track type (audio/video/etc)
    // console.debug(">>>>>>>>event: ", event.track.kind); // video, audio ..
    if(event.track.kind === "video" || event.track.kind === "audio"){
        console.debug("Adding ", event.track.kind, " track to peer connection.");
        if(event.streams.length > 0) {
            console.debug("event.streams[0].getTracks(): ", event.streams[0].getTracks());
            // 54:36 https://www.youtube.com/watch?v=QsH8FL0952k
            try{
                event.streams[0].getTracks().forEach((track) => {
                    //console.debug("Adding track: ", track);
                    myPeerConnection.addTrack(track);
                });
                } catch(e){ reportError(e) };
        }
        //myPeerConnection.addTrack(event.track);
    } else { console.warn("Track not video: ", event.track.kind); }

    // Add video track to html element
//    document.getElementById("bigScreen").srcObject = event.streams[0];
    //document.getElementById("hangup-button").disabled = false;
}*/

function handleTrackEvent(track, streams) {
    console.debug("WebRTC.handleTrackEvent(track, streams[0]) : ", track, streams[0]);
      track.onunmute = () => {
        if (document.getElementById("bigScreen").srcObject) {
          return;
        }
        document.getElementById("bigScreen").srcObject = streams[0];
      };
}

// REMOVE streams
function handleRemoveTrackEvent(event) {
    console.debug("WebRTC.handleRemoveTrackEvent() - event: ", event);
    const stream = document.getElementById("bigScreen").srcObject;
    const trackList = stream.getTracks();

    if (trackList.length === 0) {
        closeVideoCall();
    }
}


// HANGING up
function hangUpCall() {
    console.debug("WebRTC.hangUpCall() \ntargetUsername:", targetUsername);

    sendMessageToServer({
        name: myUsername,
        target: targetUsername,
        type: "hang-up",
    });

    closeVideoCall();
}


// ENDING the call
function closeVideoCall() {
    console.debug("WebRTC.closeVideoCall()");
    const remoteVideo = document.getElementById("bigScreen");
    const localVideo = document.getElementById("smallScreen");

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
        mediaConstraints.video = false;
        mediaConstraints.audio = false;
    }

    remoteVideo.removeAttribute("src");
    remoteVideo.removeAttribute("srcObject");
    localVideo.removeAttribute("src");
    localVideo.removeAttribute("srcObject");

    // document.getElementById("hangup-button").disabled = true;
    targetUsername = null;
}


// ICE CONNECTION STATE CHANGE
// call terminated from other side, etc
function handleICEConnectionStateChangeEvent(event) {
    console.debug("WebRTC.handleICEConnectionStateChangeEvent()");
    switch (myPeerConnection.iceConnectionState) {
        case "disconnected":
            console.warn("myPeerConnection was disconnected...");
            break;
        case "closed":
        case "failed":
            console.warn("myPeerConnection closed or failed...");
            closeVideoCall();
            break;
        default:
            console.warn("TODO: handle handleICEConnectionStateChangeEvent: ", event);
            break;
    }
}


// ICE SIGNALING STATE
// https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection/signalingState
function handleSignalingStateChangeEvent(event) {
    console.debug("WebRTC.handleSignalingStateChangeEvent()");
    switch (myPeerConnection.signalingState) {
        // Deprecated (replaced by closed iceConnectionState) - we watch for it only for backwards compatibility
        case "closed":
            closeVideoCall();
            break;
        case "have-local-offer":
//            console.warn("TODO: handling handleSignalingStateChangeEvent for \"have-local-offer\"");
//            console.log("SDP Offer applied successfully by setLocalDescription()");
            break;
        case "have-remote-offer":
//            console.warn("TODO: handling handleSignalingStateChangeEvent for \"have-remote-offer\"");
//            console.log("SDP Offer applied successfully by setRemoteDescription()");
            break;
        case "stable":
//            console.warn("TODO: handling handleSignalingStateChangeEvent for \"stable\"");
            break
        default:
//            console.warn("TODO: handle handleSignalingStateChangeEvent for ", myPeerConnection.signalingState);
            break;
    }
}


// ICE GATHERING STATE
// usefull for debugging or to detect when ice candidate gathering has finished
function handleICEGatheringStateChangeEvent(event) {
    console.debug("WebRTC.handleICEGatheringStateChangeEvent() ", myPeerConnection.iceGatheringState);
}


function handleGetUserMediaError(e) {
    console.debug("WebRTC.handleGetUserMediaError()");
    switch (e.name) {
        case "NotFoundError":
            console.error(
                "Unable to open your call because no camera and/or microphone" +
                "were found.",
            );
            break;
        case "SecurityError":
        case "PermissionDeniedError":
            // Do nothing; this is the same as the user canceling the call.
            break;
        default:
            console.error(`Error opening your camera and/or microphone: ${e.message}`);
            break;
    }

    closeVideoCall();
}

function reportError(e) {
    console.debug("WebRTC.reportError()");
    console.error(e.message);
}