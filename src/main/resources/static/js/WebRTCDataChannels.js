
window.addEventListener("load", main);


var myUsername, targetUsername;

// UI elements
var chatBox, fileInput;

let fileList;
//
// RTCPeerConnection
var myPeerConnectionForData;
var rtcDataChannels = new Map();
var myLocalStream, myRemoteStream;

var lastDataChannelId = 0;
//
// // WebSocket & STOMP
 var wsktForData;
 var webRtcStompClientForData;
 var webRtcSubscription001ForData;



function main(){
    console.log("WebRTCDataChannels.main()");

    chatBox = document.getElementById("chatBox");
    fileInput = document.getElementById("fileInput");

    chatBox.addEventListener("drop", dropHandler);
    fileInput.addEventListener("change", fileInputHandler);

    chatBox.addEventListener("dragover", (e) => {
        chatBox.classList.add("dragOver");
        const fileItems = [...e.dataTransfer.items].filter(
            (item) => item.kind === "file",
        );
        if (fileItems.length > 0) {
            e.preventDefault();
            if (fileItems.some((item) => item.type.startsWith("image/"))) {
                e.dataTransfer.dropEffect = "copy";
                // console.log("copy file");
            } else {
                e.dataTransfer.dropEffect = "none";
            }
        }
    });

    chatBox.addEventListener("dragleave", (e) => {
        chatBox.classList.remove("dragOver");
    });

    // Disable default browser drop-event behavior
    window.addEventListener("drop", (e) => {
        if ([...e.dataTransfer.items].some((item) => item.kind === "file")) {
            e.preventDefault();
            console.log("File open - default prevented.");
        }
        // handle dropping other things:
        e.preventDefault();
        // console.log("Something else - default prevented.");
    });

    window.addEventListener("dragover", (e) => {
        const fileItems = [...e.dataTransfer.items].filter(
            (item) => item.kind === "file",
        );
        if (fileItems.length > 0) {
            e.preventDefault();
            if (!chatBox.contains(e.target)) {
                e.dataTransfer.dropEffect = "none";
            }
        }
        // handle dropping other things:
        e.preventDefault();
        // console.log("Something else - defaults prevented again.");
    });

     /******************/
    /*  WebRTC setup  */
    myUsername = document.getElementById("emailSpan").textContent;

    wsktForData = new SockJS("/sendICEMessageForData");   // unlike WebSocket(), SockJS() provides fall-back protocols, if WebSockets are not supported in the browser
    webRtcStompClientForData = Stomp.over(wsktForData);

    // Custom subscription ID, passed to webRtcStompClientForData.subscribe ( if it's not passed, stomp automatically creates an ID )
    var myICEsubId001ForData = 'data-channel-subs-001';

    // Prepare Stomp.connect() parameters - rtcStompCallbackFunction and rtcStompConnectError:
    rtcStompCallbackFunction = function(frame){
        console.debug("WebRTCDataChannels.js rtcStompCallbackFunction()");
        webRtcSubscription001ForData = webRtcStompClientForData.subscribe("/user/queue/sendICEMessageForData", function(message){

         let parsedMsgBody = JSON.parse(message.body);

         switch (parsedMsgBody.type) {
             case "new-ice-candidate":
                 console.info("Received ice candidate...");
                 handleNewICECandidateMessageForData(parsedMsgBody);
                 break;
             case "file-transfer-req":
                console.info("Received transfer request...");
                handleFileTransferReqMessage(parsedMsgBody);
                break;
             case "file-transfer-resp":
                console.info("Received transfer response...");
                handleFileTransferRespMessage(parsedMsgBody);
                break;
             default:
                 console.warn("message type not treated: ", parsedMsgBody.type);
                 break;
            }
        }
        , {id : myICEsubId001ForData});
     }

     rtcStompConnectError = function(err){
         console.error("WebRTCDataChannels.js - STOMP  connection error: \n", err);
         reportDataError(err);
     }

     webRtcStompClientForData.connect( {}, rtcStompCallbackFunction, rtcStompConnectError);

    console.log("WebRTCDataChannels module loaded.");

}


function fileInputHandler(e){
    console.log("fileInputHandler()");
    if(fileInput.files.length > 0) {
        appendFilesToPreviewDiv(document.getElementById("fileInput").files);
    } else { console.warn("No photos selected!")}
}

function dropHandler(e){
    console.log("Drop handler triggered. Event: ", e);
    chatBox.classList.remove("dragOver");
    e.preventDefault();

    const files = [...e.dataTransfer.items]
        .map((item) => item.getAsFile())
        .filter((file) => file);
    appendFilesToPreviewDiv(files);
}


async function sendDataMessageToServer(message){
    console.debug("WebRTCDataChannels.sendDataMessageToServer()");
    let msg = JSON.stringify(message);

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
       const response = await fetch("https://cchat.ddns.net/api/webrtc/ice-server/data-message", {
//        const response = await fetch("https://localhost:8443/api/webrtc/ice-server/data-message", {
            method: "POST",
            headers: {
                credentials: "include",
                "Content-Type": "application/json; charset=UTF-8",
                token_name: token_value,
            },
            body: msg,
        });

        const result = await response;  // TODO: change to json after finishing the controller method

        // result: { type, url, status, redirected, ok, statusText, headers, body, bodyUsed }
        console.info("POST ", result.status, result.url);
    } catch (error) {
        error.message = "Error while sending data message to server:" + error.message;
        reportDataError(error);
    }
}


function appendFilesToChatScreen(files, sentOrReceived){
    //console.log("Appending files: ", files);

    toggleFilePreviewDiv();

    let articleElement = document.createElement("article");
    let headerElement = document.createElement("header");
    let footerElement = document.createElement("footer");
    let timeElement = document.createElement("time");
    let dateElement = document.createElement("date");
    let h1ContentElement = document.createElement("h1");
    let h3TimeElement = document.createElement("h3");
    let h3DateElement = document.createElement("h3");
    let pElement = document.createElement("p");

    // image list
    let imgDivElement = document.createElement("div");
    imgDivElement.style.setProperty("text-align", "center");

    articleElement.classList.add("chatArticle");
    if(sentOrReceived != "") {
        // sentOrReceived will only have "sent" or "" values - we only add the non-null value to the classlist
        articleElement.classList.add(sentOrReceived);
    }

    headerElement.classList.add("messageTimeHeader");
    footerElement.classList.add("messageDateFooter");

    pElement.classList.add("chatP");

    // let msgDate = new Date(message.timestamp);
    let msgDate = new Date();
    let dateString = msgDate.toDateString();
    let timeString = msgDate.getHours() + ":" + msgDate.getMinutes() + ":" + msgDate.getSeconds();  // TODO: make sure this is the local time

    // h3TimeElement.append("From: " + message.from + ", " + timeString);
    h3TimeElement.append("From: " + "@insert-message-source@" + ", " + timeString);
    timeElement.append(h3TimeElement);
    headerElement.append(timeElement);

    h3DateElement.append(dateString);
    dateElement.append(h3DateElement);
    footerElement.append(dateElement);

    // h1ContentElement.append(message.content);
    // h1ContentElement.append("@insert-message-content@");
    // pElement.append(h1ContentElement);

    //let imgsToFreeUrl = new Set();
    // image list:
    for (const file of files) {
        if (file.type.startsWith("image/")) {
            const img = document.createElement("img");
            img.src = URL.createObjectURL(file);
            //imgsToFreeUrl.add(img.src);
            img.alt = file.name;
            img.classList.add("chatScreenClass")
            let imgWidth = 80;
            if(files.length > 2) imgWidth = 30;
            else if(files.length === 2) imgWidth = 50;
            img.style.setProperty("width", imgWidth + "%");

            // On click, open in a big size preview
            img.addEventListener("click", (e) => {
                toggleFullScreen(e.target);
            });

            imgDivElement.appendChild(img);

            // Revoke obj url after sending file ?
//            URL.revokeObjectURL(img.src);
        }

        if(sentOrReceived === "sent"){
            console.log("Sending ", file);
//            rtcDataChannels.get(1).send(file);
//            sendFile(rtcDataChannels.get(1), file);
            sendFile(rtcDataChannels.get(file.name), file);
        }
    }

//    articleElement.append(headerElement);
    articleElement.append(imgDivElement);
//    articleElement.append(pElement);
//    articleElement.append(footerElement);

    document.getElementById('chatBox').prepend(articleElement);

//    for(imgSrc of imgsToFreeUrl){
//        console.log("revoking ", imgSrc);
//        URL.revokeObjectURL(imgSrc);
//    }
}


/**
 * Go full screen when an element (image) is clicked, etc.
 */
function toggleFullScreen(element) {
    if (!document.fullscreenElement) {
        // If the document is not in full screen mode
        // make the element full screen
        element.requestFullscreen();
        element.classList.add("fullscreenElements");
    } else {
        // Otherwise exit the full screen
        document.exitFullscreen?.();
        element.classList.remove("fullscreenElements");
    }
}


function appendFilesToPreviewDiv(files){
    console.log("appendFilesToPreviewDiv()", files);

    initRtcPeerConnection();

    toggleFilePreviewDiv();

    fileList = files;
    targetUsername = sessionStorage.getItem("iAmChattingWith");

    // image list
    let imgDivElement = document.createElement("div");
    let filePreviewDiv = document.createElement("div");
    let sendFilesButton = document.createElement("input");

    imgDivElement.setAttribute("id", "imgDivElement");

    filePreviewDiv.setAttribute("id", "filePreviewDiv");
    window.addEventListener("keydown", (e) => {
        console.info("Key event: ", e);
        if(e.key === "Escape") {
            toggleFilePreviewDiv();
            e.preventDefault();
        }
    });

    sendFilesButton.setAttribute("type", "button");
    sendFilesButton.setAttribute("id", "sendFilesBtn");
    sendFilesButton.setAttribute("value", `Send to ${targetUsername}`);
    sendFilesButton.setAttribute("disabled", "");   // enabled at dataChannel open
    sendFilesButton.addEventListener("click", (e) => {
        appendFilesToChatScreen(fileList, "sent");
        });

    // image list:
    for (const file of fileList) {
        if (file.type.startsWith("image/")) {
            const img = document.createElement("img");
            img.src = URL.createObjectURL(file);
            img.alt = file.name;
            img.classList.add("imgPreviewClass")

            let imgWidth= 80;
            if(fileList.length > 2) imgWidth = 30;
            else if(fileList.length === 2) imgWidth = 50;
            img.style.setProperty("width", imgWidth + "%");

            imgDivElement.appendChild(img);

            initDataChannel(file.name);
        }
    }

    filePreviewDiv.append(imgDivElement);
    filePreviewDiv.append(sendFilesButton);
    chatBox.prepend(filePreviewDiv);

//    initRtcPeerConnection();
//    initDataChannel();
}

      /*******************************************/
     /*  RTCPeerConnection and RTCDataChannels  */
    /*******************************************/

function initRtcPeerConnection() {
    console.debug("WebRTCDataChannels.initRtcPeerConnection()");

    if (myPeerConnectionForData) {
        console.info("An instance of myPeerConnectionForData already exists..");
        return;
    }
    console.info("Creating new peer connection for data.");

    myPeerConnectionForData = new RTCPeerConnection({
        iceServers: [
            { urls: "stun:stun.stunprotocol.org" }
            ,{ urls: "stun:stun4.l.google.com:19302" }
            ,{ urls: "stun:stun4.l.google.com:5349" }
        ],
    });

    // The first 3 event handlers are required:
    myPeerConnectionForData.onicecandidate = handleICECandidateEventForData;
    myPeerConnectionForData.ontrack = ({ track, streams }) => handleTrackEventForData(track, streams);
    myPeerConnectionForData.onnegotiationneeded = handleNegotiationNeededEventForData;
}

function initDataChannel(relatedFileName){
    console.debug("WebRTCDataChannels.initDataChannel()");

    lastDataChannelId++;
    let newDataChannel = myPeerConnectionForData.createDataChannel(
        targetUsername
        , {
//            maxRetransmits: 15
//            , negotiated: true
//            , id: lastDataChannelId
        });
//    rtcDataChannels.set(lastDataChannelId, newDataChannel);
    rtcDataChannels.set(relatedFileName, newDataChannel);

    newDataChannel.addEventListener("open", (e) => {
        console.log("New data channel OPEN: ", newDataChannel);
        //newDataChannel.send("Hey there!");
        document.getElementById("sendFilesBtn").removeAttribute("disabled");    // enable send button when channel is connected TODO better
    });

    newDataChannel.addEventListener("message", onMessage);
}


      /*********************************/
     /* * * WebRTC event handlers * * */
    /*********************************/

function handleICECandidateEventForData(e) {
    console.log("handleICECandidateEventForData(e)");

    if (e.candidate) {
            sendDataMessageToServer({
                type: "new-ice-candidate",
                target: targetUsername,
                candidate: e.candidate
            });
        }
}

function handleTrackEventForData(track, streams) {
    console.log("NOOP - handleTrackEventForData(track, streams)", track, streams);
}

function handleNegotiationNeededEventForData(e) {
    console.log("handleNegotiationNeededEventForData(e)");

    myPeerConnectionForData
        .createOffer()
        .then((offer) => myPeerConnectionForData.setLocalDescription(offer))
        .then(() => {
            sendDataMessageToServer({
                name: myUsername,
                target: targetUsername,
                type: "file-transfer-req",
                candidate: myPeerConnectionForData.localDescription
            });
        })
        .catch(reportDataError);
}


// We receive a candidate from a peer
function handleNewICECandidateMessageForData(msg) {
    console.log("handleNewICECandidateMessageForData(msg)", msg);

    initRtcPeerConnection();

    myPeerConnectionForData.addIceCandidate(msg.candidate);

    myPeerConnectionForData.ondatachannel = (e) => {
        lastDataChannelId++;
//        e.channel.onmessage = ({data}) => console.log("Received message: ", data);
        e.channel.addEventListener("message", onMessage);
        e.channel.addEventListener("open", (e) => {console.log("New peer data channel: ", e.channel);});
        rtcDataChannels.set(lastDataChannelId, e.channel);
    }
}

function handleFileTransferReqMessage(msg){
    console.log("handleFileTransferReqMessage(msg)", msg);

    targetUsername = msg.name;
    const desc = new RTCSessionDescription(msg.candidate);

    initRtcPeerConnection();

    myPeerConnectionForData.setRemoteDescription(desc);

    myPeerConnectionForData.createAnswer()
        .then((answer) => myPeerConnectionForData.setLocalDescription(answer))
        .then(() => {
            const msg = {
                name: myUsername,
                target: targetUsername,
                type: "file-transfer-resp",
                candidate: myPeerConnectionForData.localDescription
            };

            sendDataMessageToServer(msg);
        })
        .catch(reportDataError);
}

function handleFileTransferRespMessage(msg){
    console.log("handleFileTransferRespMessage(msg)", msg);

    const desc = new RTCSessionDescription(msg.candidate);
    myPeerConnectionForData.setRemoteDescription(desc);
}


// Cross-browser stable chunking scheme
// Key points:
// 1) Chunking (recommend around 16 KiB), avoid exceeding "actual available message size"
// 2) Sender backpressure control: observe bufferedAmount
// 3) Receiver reassembly: group by fileId + seq
// 4) Integrity verification: can add total length/checksum at application layer

const CHUNK = 16 * 1024 // 16 KiB - good compatibility

/**
 * Sender: chunk File/ArrayBuffer
 *
 */

/*
 * @param {RTCDataChannel} channel
 * @param {File} file
 */
async function sendFile(channel, file) {
    console.log("WebRTCDataChannels - sendFile(channel, file)");
  const fileId = crypto.randomUUID()
  const buf = await file.arrayBuffer()
  const total = buf.byteLength
  const view = new Uint8Array(buf)

  // Send header metadata (filename, size, MIME, chunk count)
  channel.send(
    JSON.stringify({
      t: "file-meta",
      id: fileId,
      name: file.name,
      size: total,
      type: file.type,
      chunks: Math.ceil(total / CHUNK)
    })
  )

  for (let offset = 0, seq = 0; offset < total; offset += CHUNK, seq++) {
    // Backpressure: control sending rate
    while (channel.bufferedAmount > 1 << 16) {
      await new Promise(r => setTimeout(r, 10));
    }
    const slice = view.subarray(offset, Math.min(offset + CHUNK, total));

    // Custom binary header: 8-byte fileId first 8 bytes + 4-byte seq (simplified, production can use more stable protocol)
    // For simplicity, using JSON header + raw binary here (two messages), balancing overhead

    channel.send(JSON.stringify({ t: "file-chunk", id: fileId, seq }));
    channel.send(slice);
  }

  channel.send(JSON.stringify({ t: "file-end", id: fileId }));
}

/*
 * Receiver: reassemble file(s)
 */
//const receiveState = Object.create(null);
//const receiveState = [null];
const receiveStateByChannelId = new Map();

/**
 * @param {MessageEvent} ev
 */
function onMessage(ev) {
    const data = ev.data;
    const dataChannelID = ev.target.id;
    if(receiveStateByChannelId.get(dataChannelID) === undefined){
        receiveStateByChannelId.set(dataChannelID,[null]);
    }
    console.log("WebRTCDataChannels - onMessage(ev),dataChannelID", ev,dataChannelID);
    if (typeof data === "string") {
        const msg = JSON.parse(data)
        if (msg.t === "file-meta") {
//            receiveState[msg.id] = {
            receiveStateByChannelId.get(dataChannelID)[msg.id] = {
                meta: msg,
                bufs: [],
                nextSeq: 0,
                size: msg.size,
                received: 0
            }
        } else if (msg.t === "file-chunk") {
            // Record the next seq that should arrive
//            receiveState[msg.id].nextSeq = msg.seq;
            receiveStateByChannelId.get(dataChannelID)[msg.id].nextSeq = msg.seq;
        } else if (msg.t === "file-end") {
//            const st = receiveState[msg.id];
            const st = receiveStateByChannelId.get(dataChannelID)[msg.id];
            // Reassemble (assuming correct order; if unordered, need to sort by seq)
            const blob = new Blob(st.bufs, { type: st.meta.type });
            // TODO: verify size/hash; trigger save or preview
            console.log("file assembled", st.meta.name, blob);
            appendFilesToChatScreen([blob]);    // TODO check the current screen is the active user or not
        }
    } else if (data instanceof ArrayBuffer || data instanceof Blob) {
        // Binary fragment
        // If using unordered channel, should sort by seq at application layer; omitted here
        // Unified conversion to ArrayBuffer
        const p = data instanceof Blob ? data.arrayBuffer() : Promise.resolve(data);
        p.then(ab => {
            // Store fragment to the most recent fileId (production should strictly associate seq -> id)
            const ids = Object.keys(receiveStateByChannelId.get(dataChannelID));
//            const last = receiveState[ids[ids.length - 1]]
            const last = receiveStateByChannelId.get(dataChannelID)[ids[ids.length - 1]]
            last.bufs.push(new Uint8Array(ab))
            last.received += ab.byteLength
        });
    }
}


      /*****************/
     /* * * Utils * * */
    /*****************/

function toggleFilePreviewDiv() {
    try {
        let filePreviewDivRemove = document.getElementById("filePreviewDiv");
        if (filePreviewDivRemove) {
            for (const img of filePreviewDivRemove.querySelectorAll("img")) {
                URL.revokeObjectURL(img.src);
            }
            fileList = null;
            document.getElementById("imgDivElement").replaceChildren();
            filePreviewDivRemove.remove();
            filePreviewDivRemove = null;
        }
    } catch (e) {
        reportDataError(e);
    }
}


function reportDataError(e) {
    console.log("WebRTCDataChannels.reportDataError()");
    console.error(e);
}
