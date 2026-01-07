
window.addEventListener("load", main);

var wss;
var stompMempoolClient;
var subscription001;


function main(){
    console.log("wss-mempool.main()\nStarting connection to mempool...");

    wss = new WebSocket("wss://mempool.space/api/v1/ws");

    stompMempoolClient = Stomp.over(wss);

    // Custom subscription ID, passed to stompClient.subscribe ( if it's not passed, stomp automatically creates an ID )
    var mysubid001 = 'mempool-sub-001';

    // Prepare Stomp.connect() parameters - stompCallbackFunction and stompConnectError:
    let stompCallbackFunction = function(frame){
        subscription001 = stompMempoolClient.subscribe(
            JSON.stringify({ "action": "want", "data": ["mempool-blocks", "stats"] })
            , function(message){
                console.log("mempool message received: " + message.body);

                let msg = JSON.parse(message.body);

                console.info("mempool parsed response: ", msg);
            }
            , {id : mysubid001});

    }

    // stompClient.subscribe() returns a subscription object containing the ide and a method:
    //      Object { id: "my-subscription-id-001", unsubscribe: unsubscribe() }
    // Use this method to unsubscribe:
    //      subscription001.unsubscribe();

    let stompConnectError = function(error){
        console.log("wss-mempool.js - STOMP protocol error: " + error);

        document.getElementById("sendTextForm").disabled = true;
        document.getElementById("writeText").disabled = true;
        document.getElementById("writeText").value = "STOMP  protocol error: " + error;
        document.getElementById("writeText").color = "red";

    }

    stompMempoolClient.connect( {}, stompCallbackFunction, stompConnectError);

    // Automatic reconnect attempts will be made every 5000 ms ( default is 0 - disabled )
    stompMempoolClient.reconnect_delay = 5000;
    // Send a heartbeat every 30 seconds
    stompMempoolClient.heartbeat.outgoing = 30000;
    // Do not receive heartbeats from the server
    //stompClient.heartbeat.incoming = 0;


    // let sendForm = function (inputText) {
    //
    //     let message = {
    //         from: currentUser,
    //         to: sessionStorage.getItem("stompClientUsernameDestination"),
    //         content: inputText,
    //         timestamp: new Date(Date.now()).toISOString()
    //     };
    //
    //     stompClient.send(sessionStorage.getItem("stompClientMessageDestination"), {}, JSON.stringify(message));  // "/app" will forward messages to the @Controller with a "/generalChat" endpoint
    //
    //     // When i send a private message, append that message to my window also
    //     if (sessionStorage.getItem("stompClientUsernameDestination") != "") {
    //         // appendSentMessage(message, "sent");
    //         appendMessageToChatScreen(message, "sent");     // TODO do better
    //     }
    // }
}

// function appendMessageToChatScreen(message, sentOrReceived){
//     let articleElement = document.createElement("article");
//     let headerElement = document.createElement("header");
//     let footerElement = document.createElement("footer");
//     let timeElement = document.createElement("time");
//     let dateElement = document.createElement("date");
//     let h1ContentElement = document.createElement("h1");
//     let h3TimeElement = document.createElement("h3");
//     let h3DateElement = document.createElement("h3");
//     let pElement = document.createElement("p");
//
//     articleElement.classList.add("chatArticle");
//     if(sentOrReceived != "") {
//         // sentOrReceived will only have "sent" or "" values - we only add the non-null value to the classlist
//         articleElement.classList.add(sentOrReceived);
//     }
//
//     headerElement.classList.add("messageTimeHeader");
//     footerElement.classList.add("messageDateFooter");
//
//     pElement.classList.add("chatP");
//
//     let msgDate = new Date(message.timestamp);
//     let dateString = msgDate.toDateString();
//     let timeString = msgDate.getHours() + ":" + msgDate.getMinutes() + ":" + msgDate.getSeconds();  // TODO: make sure this is the local time
//
//     h3TimeElement.append("From: " + message.from + ", " + timeString);
//     timeElement.append(h3TimeElement);
//     headerElement.append(timeElement);
//
//     h3DateElement.append(dateString);
//     dateElement.append(h3DateElement);
//     footerElement.append(dateElement);
//
//     h1ContentElement.append(message.content);
//     pElement.append(h1ContentElement);
//
//     articleElement.append(headerElement);
//     articleElement.append(pElement);
//     articleElement.append(footerElement);
//
//     document.getElementById('chatBox').prepend(articleElement);
// }
