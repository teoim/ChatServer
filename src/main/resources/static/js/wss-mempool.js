
window.addEventListener("load", main);

var wss;
var isPaused = true;

// mempool data
var latestConversions;

function main(){
    console.log("wss-mempool.main()\nStarting connection to mempool...");

    window.addEventListener('keydown', function(e) {
        if (e.metaKey == true && e.key.toLowerCase() == "i"){
            document.getElementById("pageFooter").hidden = !document.getElementById("pageFooter").hidden;
            isPaused = !isPaused;
    	  }
    });

    wss = new WebSocket("wss://mempool.space/api/v1/ws");

    wss.addEventListener("open", () => {
        console.info("CONNECTED to mempool.");

        console.log(`wss-mempool.main() - Requesting mempool-blocks and stats.`);
        // wss.send(JSON.stringify({ "action": "want", "data": ["mempool-blocks", "stats"] }))
        wss.send(JSON.stringify({ "action": "want", "data": ["stats"] }))
    });

    wss.addEventListener("message", (e) => {
        if(isPaused) return;
        const message = JSON.parse(e.data);
        console.info("RECEIVED message:", message);
        if(message.conversions) {
            updateConversions(message.conversions);
            return; // when we get the conversions, there are no fees data
        }
        updateFeesInPage(message.fees);
    });

    wss.addEventListener("close", () => {
        console.info("DISCONNECTED from mempool");
    });
}

function updateFeesInPage(fees){
    console.log("updateFeesInPage()");

    document.getElementById("p1").replaceChildren(fees.fastestFee);
    document.getElementById("p2").replaceChildren(fees.halfHourFee);
    document.getElementById("p3").replaceChildren(fees.hourFee);
    document.getElementById("p4").replaceChildren(fees.economyFee);
    document.getElementById("p5").replaceChildren(fees.minimumFee);

}

function updateConversions(conversions){
    console.log("updateConversions()");
    latestConversions = conversions;

    let conversionsSelect = document.getElementById("conversionsEntries");
    conversionsSelect.replaceChildren();
    conversionsSelect.addEventListener("change", function(event) {
        document.getElementById("btcPrice").replaceChildren(": " + event.target.value);
    });
    for(let entry in conversions){
        if (entry === "time") {
            let timeElement = document.getElementById("btcPriceTime");
            timeElement.replaceChildren();
            timeElement.style.setProperty("display", "inline-block");
            timeElement.append( " @ " + new Date(conversions.time * 1000).toISOString().slice(11, 19));
            document.getElementById("conversionsArticle").appendChild(timeElement);
        }
        else {
            let optionElement = document.createElement("option");
            optionElement.value = conversions[entry];
            optionElement.innerHTML = entry;
            conversionsSelect.appendChild(optionElement);
        }
    }
    document.getElementById("btcPrice").replaceChildren(": " + conversionsSelect.value);    // set first price value
    document.getElementById("btcPrice").style.setProperty("display", "inline-block");
    document.getElementById("conversionsArticle").style.setProperty("display", "inline-block");
}