
window.onload = main;

let logoutBtn;

function main(){
    logoutBtn = document.getElementById("logoutBtn");

    logoutBtn.addEventListener("click", (event) => { sessionStorage.clear(); });

    if(screen.availWidth <= 1200){
//        document.getElementById("friendList").append(document.getElementById("logoutForm"));
        document.getElementById("friendList").prepend(document.getElementById("pageHeader"));
        document.getElementById("searchDiv").parentNode.insertBefore(
            document.getElementById("searchResultsDiv")
            , document.getElementById("searchDiv").nextSibling);
    }
}