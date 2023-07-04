
window.onload = main;

var ws;
var stompClient;
var subscription001;
var subscription002;
var stompClientMessageDestination = "/app/generalChat"
var stompClientUsernameDestination = "";

var sendButton;
var messageInputBox;

var currentUser = "";


function main(){
    console.log("Page loaded, starting WebSockets and STOMP...");

    currentUser = document.getElementById("emailSpan").textContent;

    document.getElementById('logoutForm').onsubmit = logout;

    //ws = new WebSocket("ws://localhost:8080/generalChat");
    ws = new SockJS("/generalChat");   // unlike WebSocket(), SockJS() provides fall-back protocols, if WebSockets are not supported in the browser

    stompClient = Stomp.over(ws);

    sendButton = document.getElementById("submitBtn");
    messageInputBox = document.getElementById("writeText");

    focusOnMessageInputBox();

    // Custom subscription ID, passed to stompClient.subscribe ( if it's not passed, stomp automatically creates an ID )
    var mysubid001 = 'my-subscription-id-001';
    var mysubid002 = 'my-subscription-id-002';

    // Prepare Stomp.connect() parameters - stompCallbackFunction and stompConnectError:
    stompCallbackFunction = function(frame){
        subscription001 = stompClient.subscribe("/topic/generalChat", function(message){     //    "/topic/generalChat" is a broker destination
            console.log("stompClient received general message: " + message.body);

            receivedMessage(message, "general");
        }
        , {id : mysubid001});

        subscription002 = stompClient.subscribe("/user/queue/sendPrivateText", function(message){
                console.log("stompClient received private message: " + message);

                receivedMessage(message, "private");
            }
            , {id : mysubid002});
    }

    // stompClient.subscribe() returns a subscription object containing the ide and a method:
    //      Object { id: "my-subscription-id-001", unsubscribe: unsubscribe() }
    // Use this method to unsubscribe:
    //      subscription001.unsubscribe();

    stompConnectError = function(error){
        console.log("STOMP  protocol error: " + error);

        document.getElementById("sendTextForm").disabled = true;
        document.getElementById("writeText").disabled = true;
        document.getElementById("writeText").value = "STOMP  protocol error: " + error;
        document.getElementById("writeText").color = "red";

    }

    stompClient.connect( {}, stompCallbackFunction, stompConnectError);

    // Automatic reconnect attempts will be made every 5000 ms ( default is 0 - disabled )
    //stompClient.reconnect_delay = 5000;
    // Send a heartbeat every 30 seconds
    //stompClient.heartbeat.outgoing = 30000;
    // Do not receive heartbeats from the server
    //stompClient.heartbeat.incoming = 0;


    sendButton.addEventListener("click", (event) => {
        var inputText = document.getElementById("writeText").value;
        if(inputText !== "") {
            sendForm(inputText);
        }
    });

    messageInputBox.addEventListener("keydown", function (e) {
        var inputText = document.getElementById("writeText").value;
        if (e.code === "Enter" && inputText !== "") {
            e.preventDefault();
            sendForm(inputText);
        }
    });

    sendForm = function(inputText){
        // stompClient.send("/app/generalChat", {}, document.getElementById("writeText").value);  // "/app" will farward messages to the @Controller with a "/generalChat" endpoint
        //stompClient.send("/app/generalChat", {}, inputText);  // "/app" will forward messages to the @Controller with a "/generalChat" endpoint

        let message = { from : currentUser, to : stompClientUsernameDestination, content : inputText, timestamp : Date.now() };

        //stompClient.send(stompClientMessageDestination, {}, inputText);  // "/app" will forward messages to the @Controller with a "/generalChat" endpoint
        stompClient.send(stompClientMessageDestination, {}, JSON.stringify(message));  // "/app" will forward messages to the @Controller with a "/generalChat" endpoint

        messageInputBox.value = "";
        focusOnMessageInputBox();
    }

    setFriendsListEventListener();

}

function receivedMessage(message, channelType){
    let article = document.createElement("article");
    article.classList.add("chatArticle");
    article.classList.add(channelType);   // "general" or "private" chat
    let p = document.createElement("p");
    p.classList.add("chatP");

    var data = JSON.parse(message.body);
    p.append("From: " + data.from + "\n " + data.content + "\n" + data.timestamp);
    article.append(p);
    document.getElementById('chatBox').append(article);
}

function setFriendsListEventListener(){

    var friends = [];

    var listOfFriendsH3 = document.getElementsByClassName("friendsListH3");

    for ( var i = 0; i < listOfFriendsH3.length; i++ ) {
        let usernameEmail = listOfFriendsH3[i].innerText

        listOfFriendsH3[i].addEventListener("click", (event) => {
            stompClientMessageDestination = "/app/sendPrivateText";
            stompClientUsernameDestination = usernameEmail;
            loadUserChats(usernameEmail);
            focusOnMessageInputBox();
        });
    }

    // Set general chat click listener
    document.getElementById("friendsListGeneralH3").addEventListener("click", () => {
        stompClientMessageDestination = "/app/generalChat";
        stompClientUsernameDestination = "";
        loadGeneralChats();
        focusOnMessageInputBox()
    });

}

function loadUserChats(username){
    console.log( "Loading user chats for " + username );
    // TODO
}

function loadGeneralChats(){
    console.log( "Loading general chats ");
    // TODO
}
function focusOnMessageInputBox(){
    messageInputBox.focus();
}

function logout(){
    stompClient.disconnect();
    console.log("logout() - STOMP client disconnected.");
}