
// window.onload = main;
window.addEventListener("load", main);
// window.addEventListener("DOMContentLoaded", main);   // triggeres after dom elements are loaded

var ws;
var stompClient;
var subscription001;
var subscription002;
var stompClientMessageDestination = "/app/generalChat"
var stompClientUsernameDestination = "";

var sendButton;
var messageInputBox;

var currentUser;
var lastMessageFetchedTimestamp = new Map();    // lastMessageFetchedTimestamp(username : lastTimestamp)
var iAmChattingWith = "generalChat";

const cachedTextMessagesWithUser = new Map();
const textMessagesToUrl = "http://localhost:8080/messages-with"
const generalChatUrl = "http://localhost:8080/general-chat"

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

            let msg = JSON.parse(message.body);

            if(iAmChattingWith == "generalChat") {
                // console.log(msg.to);
                appendMessageToChatScreen(msg, "general");
            } else {
                cacheReceivedMessage(msg);
            }
        }
        , {id : mysubid001});

        subscription002 = stompClient.subscribe("/user/queue/sendPrivateText", function(message){
            console.log("stompClient received private message: " + message);

            let msg = JSON.parse(message.body);

            if(iAmChattingWith == msg.from){
                console.log(msg.from);
                appendMessageToChatScreen(msg, "");
            } else {
                cacheReceivedMessage(msg);
            }
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

        let message = { from : currentUser, to : stompClientUsernameDestination, content : inputText, timestamp : new Date(Date.now()).toISOString() };

        //stompClient.send(stompClientMessageDestination, {}, inputText);  // "/app" will forward messages to the @Controller with a "/generalChat" endpoint
        stompClient.send(stompClientMessageDestination, {}, JSON.stringify(message));  // "/app" will forward messages to the @Controller with a "/generalChat" endpoint

        // When i send a private message, append that message to my window also
        if( stompClientUsernameDestination != "") {
            // appendSentMessage(message, "sent");
            appendMessageToChatScreen(message, "sent");     // TODO do better
        }

        messageInputBox.value = "";
        focusOnMessageInputBox();
    }

    setFriendsListEventListener();

}

function appendMessageToChatScreen(message, sentOrReceived){
    let articleElement = document.createElement("article");
    let headerElement = document.createElement("header");
    let footerElement = document.createElement("footer");
    let timeElement = document.createElement("time");
    let dateElement = document.createElement("date");
    let h1ContentElement = document.createElement("h1");
    let h3TimeElement = document.createElement("h3");
    let h3DateElement = document.createElement("h3");
    let pElement = document.createElement("p");

    articleElement.classList.add("chatArticle");
    if(sentOrReceived != "") {
        // sentOrReceived will only have "sent" or "" values - we only add the non-null value to the classlist
        articleElement.classList.add(sentOrReceived);
    }

    headerElement.classList.add("messageTimeHeader");
    footerElement.classList.add("messageDateFooter");

    pElement.classList.add("chatP");

    let msgDate = new Date(message.timestamp);
    let dateString = msgDate.toDateString();
    let timeString = msgDate.getHours() + ":" + msgDate.getMinutes() + ":" + msgDate.getSeconds();  // TODO: make sure this is the local time

    h3TimeElement.append("From: " + message.from + ", " + timeString);
    timeElement.append(h3TimeElement);
    headerElement.append(timeElement);

    h3DateElement.append(dateString);
    dateElement.append(h3DateElement);
    footerElement.append(dateElement);

    h1ContentElement.append(message.content);
    pElement.append(h1ContentElement);

    articleElement.append(headerElement);
    articleElement.append(pElement);
    articleElement.append(footerElement);

    document.getElementById('chatBox').prepend(articleElement);
}

function cacheReceivedMessage(message){
    var timestampedMapOfMessages;
    var msgFromUser = message.from;

    if(cachedTextMessagesWithUser.has(msgFromUser)){
        timestampedMapOfMessages = cachedTextMessagesWithUser.get(msgFromUser);
    } else {
        timestampedMapOfMessages = new Map();
    }

    timestampedMapOfMessages.set( message.timestamp, message);
    lastMessageFetchedTimestamp.set(msgFromUser, message.timestamp);

    cachedTextMessagesWithUser.set(msgFromUser, timestampedMapOfMessages);
    console.log("New messages from " + msgFromUser + ". TODO: call function to blink " + msgFromUser + " chat box link.")
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
            iAmChattingWith = usernameEmail;
            focusOnMessageInputBox();
        });
    }

    // Set general chat click listener
    document.getElementById("friendsListGeneralH3").addEventListener("click", () => {
        stompClientMessageDestination = "/app/generalChat";
        stompClientUsernameDestination = "";
        iAmChattingWith = "generalChat";
        loadGeneralChats();
        focusOnMessageInputBox()
    });

}

function loadUserChats(username){
    console.log( "Loading user chats for " + username );

    // Clear current chat screen:
    document.getElementById('chatBox').replaceChildren();

    // Build API url:
    let toUserTextMessagesFinalURL = textMessagesToUrl + "/" + username;

    if(lastMessageFetchedTimestamp.has(username)){
        toUserTextMessagesFinalURL = toUserTextMessagesFinalURL + "/" + lastMessageFetchedTimestamp.get(username);
        console.log("API URL toUserTextMessagesFinalURL : " + toUserTextMessagesFinalURL);
    }

    // Fetch messages from database through API call:
    $.get( toUserTextMessagesFinalURL, function(data, status){
        if(status=="success"){
            console.log("Chats successfully fetched from DB. " + data.length + " messages fetched.");
            /** timestampedMapOfMessages( timestamp : message[from,to,content,timestamp] )    */
            let timestampedMapOfMessages = new Map();
            if(cachedTextMessagesWithUser.has(username)){
                timestampedMapOfMessages = cachedTextMessagesWithUser.get(username);
            }

            for( let i = 0; i < data.length; i++){
                console.log(data[i]);
                let msg = data[i];
                if(timestampedMapOfMessages.has( msg.timestamp)) break;
                timestampedMapOfMessages.set( msg.timestamp, msg);
                // appendSentMessage( msg, "general");
                lastMessageFetchedTimestamp.set(username, msg.timestamp);
            }
            cachedTextMessagesWithUser.set( username, timestampedMapOfMessages);
            timestampedMapOfMessages.forEach( function(value, key, map){
               // appendSentMessage(value, "sent");  // [TODO:improvement?] for now append it as a general message, for the css styling
                if(value.from == currentUser) {
                    // Message sent by the current user
                    appendMessageToChatScreen(value, "sent");  // [TODO:improvement?] for now append it as a general message, for the css styling
                } else {
                    // Message received by the current user
                    appendMessageToChatScreen(value, "");
                }
            });
            console.log("Chats local map complete.\nLast message: " + lastMessageFetchedTimestamp.get(username));
        }
    });
}

function loadGeneralChats(){
    console.log( "Loading general chats ");

    // TODO refactor this method and loadUserChats(username) [reduce duplicate code, etc]

    // Clear current chat screen:
    document.getElementById('chatBox').replaceChildren();

    // Build API url:
    let toUserTextMessagesFinalURL = generalChatUrl + "/" + iAmChattingWith;    // iAmChattingWith should be "generalChat"

    if(lastMessageFetchedTimestamp.has(iAmChattingWith)){
        toUserTextMessagesFinalURL = toUserTextMessagesFinalURL + "/" + lastMessageFetchedTimestamp.get(iAmChattingWith);
        console.log("API URL toUserTextMessagesFinalURL : " + toUserTextMessagesFinalURL);
    }

    // Fetch messages from database through API call:
    $.get( toUserTextMessagesFinalURL, function(data, status){
        if(status=="success"){
            console.log("Chats successfully fetched from DB. " + data.length + " messages fetched.");
            /** timestampedMapOfMessages( timestamp : message[from,to,content,timestamp] )    */
            let timestampedMapOfMessages = new Map();
            if(cachedTextMessagesWithUser.has(iAmChattingWith)){
                timestampedMapOfMessages = cachedTextMessagesWithUser.get(iAmChattingWith);
            }

            for( let i = 0; i < data.length; i++){
                console.log(data[i]);
                let msg = data[i];
                if(timestampedMapOfMessages.has( msg.timestamp)) break;
                timestampedMapOfMessages.set( msg.timestamp, msg);
                // appendSentMessage( msg, "general");
                lastMessageFetchedTimestamp.set(iAmChattingWith, msg.timestamp);
            }
            cachedTextMessagesWithUser.set( iAmChattingWith, timestampedMapOfMessages);
            timestampedMapOfMessages.forEach( function(value, key, map){
                // appendSentMessage(value, "sent");  // [TODO:improvement?] for now append it as a general message, for the css styling
                if(value.from == currentUser) {
                    // Message sent by the current user
                    appendMessageToChatScreen(value, "sent");  // [TODO:improvement?] for now append it as a general message, for the css styling
                } else {
                    // Message received by the current user
                    appendMessageToChatScreen(value, "");
                }
            });
            console.log("Chats local map complete.\nLast message: " + lastMessageFetchedTimestamp.get(iAmChattingWith));
        }
    });
}

function focusOnMessageInputBox(){
    messageInputBox.focus();
}

function logout(){
    stompClient.disconnect();
    console.log("logout() - STOMP client disconnected.");
}