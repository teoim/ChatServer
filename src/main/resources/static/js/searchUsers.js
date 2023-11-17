

window.addEventListener("load", main);  // triggered after dom elements AND images, stylesheets, fonts etc are loaded

var searchInputObj
var searchResultsDiv
var getSearchUserUrl = "http://localhost:8080/searchUserByEmailOrNick"
var addUserToFriendListUrl = "http://localhost:8080/addUserToFriendsList"
function main() {
    searchInputObj = document.getElementById("searchInput");
    searchInputObj.addEventListener("input", searchUsers);
    searchInputObj.addEventListener("focusin", searchUsers);
    //searchInputObj.addEventListener("focusout", clearSearchResults);

    searchResultsDiv = document.getElementById("searchResultsDiv");
}


async function searchUsers() {
    //if(Math.floor(Math.random() * 500) + 200 < 400 ) { console.log("X"); return;}     // randomly stop api calls to reduce server load // TODO: do better
    //await delay(500);       // Wait 1/2 second before each get

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
    link.addEventListener("click", addFriend);
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
    let userEmail = event.target.text.split(", ")[1];
    console.log("Adding " + userEmail + " as friend.");

    clearSearchResults();

    //let addFriendURL = addUserToFriendListUrl   + "/" + userEmail;

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

    /*$.post("http://localhost:8080/api/webrtc/message", JSON.stringify(message), (data, status) => {
        console.log(data);
    });*/

}
