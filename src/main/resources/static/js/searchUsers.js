

window.addEventListener("load", main);  // triggered after dom elements AND images, stylesheets, fonts etc are loaded

var searchInputObj
var searchResultsDiv
var getSearchUserUrl = "http://localhost:8080/searchUserByEmailOrNick"
var addUserToFriendListUrl = "http://localhost:8080/addUserToFriendsList"
var blockUserUrl = "http://localhost:8080/blockUser"
var addFriendIcon
var blockUserIcon

function main() {
    searchInputObj = document.getElementById("searchInput");
    searchInputObj.addEventListener("input", searchUsers);
    searchInputObj.addEventListener("focusin", searchUsers);
    //searchInputObj.addEventListener("focusout", clearSearchResults);

    searchResultsDiv = document.getElementById("searchResultsDiv");

    addFriendIcon = document.getElementById("addFriendIcon");
    addFriendIcon.addEventListener("click", addFriend);

    blockUserIcon = document.getElementById("blockUserIcon");
    blockUserIcon.addEventListener("click", blockUser);
}


async function searchUsers() {

    clearSearchResults();

    let searchString = searchInputObj.value;
    if (searchString == "") {
        clearSearchResults();
        return;
    }
    //console.log("Searching for:\n" + searchString);

    let updateGetSearchUserUrl = getSearchUserUrl + "/" + searchString;

    // Search users in the database through API calls:
    $.get( updateGetSearchUserUrl, function(data, status){
        if(status==="success"){
            //console.log("DB Search returned " + data.length + " possible matches.");
            for( let i = 0; i < data.length; i++){
                console.log(data[i]);
                let email = data[i].email;
                let nick = data[i].nick;
                let bio = data[i].bio;
                let name = data[i].name;
                let surname = data[i].surname;
                let profilePhotoLink = data[i].profilePhotoLink;
                //console.log("Possible match: " + email + "\t" + nick + "\t" + bio + "\t" + profilePhotoLink);

                appendResultToClientList(data[i]);
            }

            console.log("User search complete.");
        }
    });
}


function appendResultToClientList(resultObj){
    let resultArticle = document.createElement("article");
    let headerElement = document.createElement("header");
    let footerElement = document.createElement("footer");
    let h6HeaderElement = document.createElement("h6");
    let h6FooterElement = document.createElement("h6");
    let h4Element = document.createElement("h4");
    let link = document.createElement("a");
    let profilePicture = document.createElement("img");

    link.append( document.createTextNode( resultObj.nick + ", " + resultObj.email));
    link.setAttribute('href', "#");
    link.classList.add("userSearchResultLink");
//    link.addEventListener("click", addFriend);
    link.addEventListener("click", addArticleToContactsListAndStartChatting);
    h4Element.append( link);

    h6HeaderElement.append( document.createTextNode( resultObj.name + " " + resultObj.surname));
    h6FooterElement.append( document.createTextNode( resultObj.nick + ": \"" + resultObj.bio + "\""));

    headerElement.append( h6HeaderElement);
    footerElement.append( h6FooterElement);

    profilePicture.classList.add("searchResultProfilePicture");
    profilePicture.setAttribute("src", resultObj.profilePhotoLink);

    resultArticle.append( profilePicture);
    resultArticle.append( headerElement);
    resultArticle.append( h4Element);
    resultArticle.append( footerElement);

    resultArticle.classList.add( "searchResultArticles");

    searchResultsDiv.append( resultArticle);
}


function delay(milliseconds){
    return new Promise(resolve => {
        setTimeout(resolve, milliseconds);
    });
}


function clearSearchResults(){
    searchResultsDiv.replaceChildren();
}


async function addFriend(event){
    let userEmail = "";
    try{
        userEmail = sessionStorage.getItem("iAmChattingWith");
        console.log("SessionStorage email: " + userEmail);
        if (userEmail === "" || userEmail === null) { userEmail = event.target.text.split(", ")[1]; }      // Email extracted from href text "nick, email"
        } catch(error){
            console.log("Error: " + error);
            console.log("Trying to get user email from html hidden field iAmChattingWith:");
            userEmail = document.getElementById("iAmChattingWith").value;
            if(userEmail != "" ) {
                console.log("User email found: " + userEmail);
                }
                else {
                    console.log("User email not found!");
                    }
        }

    if (userEmail === "" || userEmail === null) { return; }

    console.log("Adding " + userEmail + " as friend.");

    clearSearchResults();

    try {
        const response = await fetch( addUserToFriendListUrl, {
            method: "PUT",
            headers: {
                credentials: "include",
                // mode: "no-cors",
                "Content-Type": "application/json; charset=UTF-8",
            },
            //body: JSON.stringify(userEmail),
            body: userEmail,
        });

        // const result = await response.json();
        const result = await response;
        console.log("Success [Add user to friend list] : ", result);
    } catch (error) {
        console.error("Error [Add user to friend list] : ", error);
    }

}

async function blockUser(event){
    console.log("searchusers.js - blockUser()");
    let userEmail = "";
    try{
        userEmail = sessionStorage.getItem("iAmChattingWith");
        if (userEmail === "" || userEmail === null) { userEmail = event.target.text.split(", ")[1]; }       // Email extracted from href text "nick, email"
        } catch(error){
            console.log("Error: " + error);
            console.log("Trying to get user email from html hidden field iAmChattingWith:");
            userEmail = document.getElementById("iAmChattingWith").value;
            if(userEmail != "" ) {
                console.log("User email found: " + userEmail);
                }
                else {
                    console.log("User email not found!");
                    }
        }

    if (userEmail === "" || userEmail === null) { return; }

    console.log("Blocking user " + userEmail + " and removing from friends list.");

    clearSearchResults();


    try {
        const response = await fetch( blockUserUrl, {
            method: "PUT",
            headers: {
                credentials: "include",
                // mode: "no-cors",
                "Content-Type": "application/json; charset=UTF-8",
            },
            //body: JSON.stringify(userEmail),
            body: userEmail,
        });

        // const result = await response.json();
        const result = await response;
        console.log("Success [Block user] : ", result.body);
    } catch (error) {
        console.error("Error [Block user] : ", error);
    }

}

function addArticleToContactsListAndStartChatting(event){
    let userEmail = event.target.text.split(", ")[1];
    console.log("Starting a chat with " + userEmail + ".");

    clearSearchResults();

    let articleElement = document.createElement("article");
    let h3Element = document.createElement("h3");
    let link = document.createElement("a");

    articleElement.classList.add("friendsListArticle");

    h3Element.classList.add("friendsListH3");
    h3Element.append(userEmail);

    link.setAttribute('href', "#");
    link.classList.add("friendsAnchor");

    link.append(h3Element);
    articleElement.append(link);

    document.getElementById('friendList').append(articleElement);

    loadUserChats(userEmail);

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
            document.getElementById("iAmChattingWith").value=username;
            sessionStorage.setItem("iAmChattingWith", username);
            sessionStorage.setItem("stompClientMessageDestination", "/app/sendPrivateText");
            sessionStorage.setItem("stompClientUsernameDestination", username);
        }
    });
    document.getElementById("writeText").focus();
}