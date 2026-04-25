
window.addEventListener("load", main);

function main() {
    document.getElementById("errMsgDiv")
        .addEventListener("click", (event) => {
            event.target.remove();
        });
}
