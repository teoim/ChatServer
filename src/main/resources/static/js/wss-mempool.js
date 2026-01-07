
window.addEventListener("load", main);

var wss;

function main(){
    console.log("wss-mempool.main()\nStarting connection to mempool...");

    wss = new WebSocket("wss://mempool.space/api/v1/ws");

    //stompMempoolClient = Stomp.over(wss);

    wss.addEventListener("open", () => {
        console.info("CONNECTED to mempool.");

        console.log(`wss-mempool.main() - Requesting mempool-blocks and stats.`);
        // wss.send(JSON.stringify({ "action": "want", "data": ["mempool-blocks", "stats"] }))
        wss.send(JSON.stringify({ "action": "want", "data": ["stats"] }))
    });

    wss.addEventListener("message", (e) => {
        const message = JSON.parse(e.data);
        const timestamp = JSON.parse(e.timeStamp)
        console.info("RECEIVED message timestamp:", timestamp);
        console.info("RECEIVED message:", message);
        console.info("RECEIVED message.da:", message.da);
    });

    wss.addEventListener("close", () => {
        console.ingo("DISCONNECTED from mempool");
    });
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
