/**
 * @author Ben Griffiths and Sorin Olteanu
 * @since 27/03/2019
 */

var stopwatches = [];
var old_count = 0;
var count = 0;

/**
 * Ran when the document is loaded.
 */
function onLoad() {
    checkOrders();
    checkAlerts();
}

/**
 * Check for updates in the orders.
 */
function checkOrders() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                count = parseInt(asyncRequest.responseText);
                if (old_count != count) {
                    old_count = count;
                    fillOrders(count);
                }
                setTimeout(function () {
                    checkOrders();
                }, 3000);
            }
        }, false);
        asyncRequest.open('GET', '/getorders?type=count&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Check Orders" + exception.toString());
    }
}


document.addEventListener('DOMContentLoaded', onLoad);

/**
 * Changes the opacity  of the button (disabling the button)
 * @param button
 */
function disableButton(button) {
    button.style.opacity = 0.6;
}

/**
 * Sends a request to the server labelling the order as cooked.
 * @param orderid id of the order.
 */
function cookedOrder(orderid) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id=' + orderid + '&type=cooked&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Change status to cooked" + exception.toString());
    }
}

/**
 * Sends a request to the server labelling the order as not cooked.
 * @param orderid id of the order.
 */
function cancelCookedOrder(orderid) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id=' + orderid + '&type=accept&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Change status to accept" + exception.toString());
    }
}

/**
 * Assigns the functins the order button when it is clicked and un-clicked.
 * @param caller
 * @param target button that was clicked
 */
function clickButton(caller, target) {
    alert(caller.id.slice(-1));
    if (caller.textContent == "\u2713") {
        caller.innerText = "x";
        disableButton(target);
        cookedOrder(parseInt(caller.id.slice(-1)));
    } else {
        caller.innerText = "\u2713";
        enable(target);
        cancelCookedOrder(parseInt(caller.id.slice(-1)));
    }
}

/**
 * Changes the opacity  of the button (enabling the button)
 * @param button
 */
function enable(button) {
    button.style.opacity = 1;
}

/**
 * Creates a button for each order.
 * @param target button to add the button to.
 * @param id of the order.
 */
function createToggleButton(target, id) {
    var div = document.getElementById(target.id.slice(1));
    var button = document.createElement("button");
    var text = document.createTextNode("\u2713");
    button.classList.add("roundButton");
    button.id = "togglebtn" + id;
    button.appendChild(text);
    button.addEventListener("click", function () {
        clickButton(button, target);
    });
    if (document.body != null) {
        div.append(button);
    }
    // button.style.left = target.offsetLeft;
    // button.style.top = target.offsetTop + 100;
}

/**
 * Gets the information of one order from the server and store it in a html tag.
 * @param orderid id of the order
 * @param parameter parameter to get from the order
 * @param id html tag id
 */
function loadOrder(orderid, parameter, id) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            setHtml(asyncRequest, id);
        }, false);
        asyncRequest.open('GET', '/getorders?id=' + orderid + '&type=' + parameter + '&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Order");
    }
}

/**
 * Gets the information needed for the order stopwatch from the server.
 * @param orderid id of the order.
 * @param parameter of the order.
 */
function loadOrderStopwatch(orderid, parameter) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            setStopwatch(asyncRequest, orderid);
        }, false);
        asyncRequest.open('GET', '/getorders?id=' + orderid + '&type=' + parameter + '&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert(exception.toString());
    }
}

/**
 * Sets the stopwatch to the information gathered from the server.
 * @param asyncRequest contains the response from the server.
 * @param orderid id of the order.
 */
function setStopwatch(asyncRequest, orderid) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var date = asyncRequest.responseText.split(":");
        stopwatches[orderid].setstart(date);
    }
}

/**
 * Fills the html with the information from every order from the server.
 * @param size (ammount of orders)
 */
function fillOrders(size) {
    var out = "";
    for (i = 0; i < size; i++) {
        out += "<div id='order" + i + "' class='order'>";
        out += "  <button id='border" + i + "' onclick='toogle(this)' class='box item'>";
        out += "  <h2>Order " + (i + 1) + "</h2>";
        out += "  <div id='order_text" + i + "' class='order_text'></div>";
        //out += " <br>";
        out += "  <span class='comment' id='comment" + i + "'></span>";
        out += "  <h3>";
        out += "  <div id='sw" + i + "' class='stopwatch'></div>";
        out += "  </h3>"
        out += "  </button>";
        out += "</div>";
    }
    document.getElementById("order_contents").innerHTML = out;
    for (i = 0; i < size; i++) {
        loadOrder(i, "itemnames", "order_text" + i);
        loadOrder(i, "comment", "comment" + i);
        createToggleButton(document.getElementById("border" + i.toString()), i);
        stopwatches.push(new Stopwatch(document.querySelector('#sw' + i), document.querySelector('.results'), "border" + i));
        loadOrderStopwatch(i, "date");

    }
}

/**
 * Toggles the button
 * @param button
 */
function toogle(button) {
    var id = button.id;
    clickButton(document.getElementById("togglebtn" + id.slice(-1)), button)
}

/**
 * Sets the contents of the html tag to the response from the server.
 * @param asyncRequest contains the response from the server.
 * @param id of the html tag.
 */
function setHtml(asyncRequest, id) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        document.getElementById(id).innerHTML = asyncRequest.responseText.split(',').join('<br>');
    }
}

/**
 * Checks for alerts from the server, re-runs every 3 seconds.
 */
function checkAlerts() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                if (asyncRequest.responseText != "") {
                    window.alert(asyncRequest.responseText);
                }
                setTimeout(function () {
                    checkAlerts();
                }, 3000);
            }
        }, false);
        asyncRequest.open('GET', '/alerts?', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Check Orders" + exception.toString());
    }
}

/**
 * Creates a request for an alert to all waiters from the server.
 */
function callWaiter() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/alerts?message=kitchen staff need help!&role=waiter', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert(exception.toString());
    }
}
