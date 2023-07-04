
window.onload = main;

var spanLogIn;
// var spanPlease;

function main(){
    spanLogIn = document.getElementById("spanLogIn");
    // spanPlease = document.getElementById("spanPlease");

    executeAsynchronously( spanLogInVisualEffect(spanLogIn));
    // executeAsynchronously( spanLogInVisualEffect(spanPlease));
}

async function spanLogInVisualEffect(htmlElement){
    while(true) {
        htmlElement.hidden = !htmlElement.hidden;
        await delay(Math.floor(Math.random() * 1000) + 200);    // random number in the 200 - 1200 interval
    }
}

// Run a function asynchronously ( ~ new thread )
function executeAsynchronously(func){
    setTimeout(func, 0);
}

function delay(milliseconds){
    return new Promise(resolve => {
        setTimeout(resolve, milliseconds);
    });
}