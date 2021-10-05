var stopwatches = [];
var old_count = 0;
var count = 0;

function onLoad() {
    checkOrders();
    checkAlerts();
}

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

function disableButton(button) {
    button.style.opacity = 0.6;
}

function cookedOrder(orderid) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id='+orderid+'&type=cooked&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Change status to cooked" + exception.toString());
    }
}

function cancelCookedOrder(orderid) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id='+orderid+'&type=accept&filter=Confirmed', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Change status to accept" + exception.toString());
    }
}

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

function enable(target) {
    target.style.opacity = 1;
}

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

function setStopwatch(asyncRequest, orderid) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var date = asyncRequest.responseText.split(":");
        stopwatches[orderid].setstart(date);
    }
}

function fillOrders(size) {
    var out = "";
    for (i = 0; i < size; i++) {
        out += "<div id='order" + i + "' class='order'>";
        out += "  <button id='border" + i + "' onclick='toogle(this)' class='box item'>";
        out += "  <h2>Order " + (i + 1) + "</h2>";
        out += "  <div id='order_text" + i + "' class='order_text'>jalapeno,2,3,2,4,6,7</div>".split(",").join("<br>");
        out += "  <h3>";
        out += "  <div id='sw" + i + "' class='stopwatch'></div>";
        out += "  </h3>"
        out += "  </button>";
        out += "</div>";
    }
    document.getElementById("order_contents").innerHTML = out;
    for (i = 0; i < size; i++) {
        loadOrder(i, "itemnames", "order_text" + i);
        createToggleButton(document.getElementById("border" + i.toString()), i);
        stopwatches.push(new Stopwatch(document.querySelector('#sw' + i), document.querySelector('.results'), "border" + i));
        loadOrderStopwatch(i, "date");

    }
}

fillOrders(15);

function toogle(button) {
    var id = button.id;
    clickButton(document.getElementById("togglebtn" + id.slice(-1)), button)
}

function setHtml(asyncRequest, id) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        document.getElementById(id).innerHTML = asyncRequest.responseText.replace(',', '\n');
    }
}

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
