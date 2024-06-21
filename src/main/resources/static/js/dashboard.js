
window.onload = main;

let logoutBtn;

function main(){
    logoutBtn = document.getElementById("logoutBtn");

    logoutBtn.addEventListener("click", (event) => { sessionStorage.clear(); });
}