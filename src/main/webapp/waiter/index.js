/**
 * This document contains all the scripts run by the waiter page.
 * @author Sorin Olteanu and Ben Griffiths
 */
stopwatches = []
size = -1;


/**
 * Ran the document is loaded
 * -loads the orders
 * -starts the check update cycle
 * -starts the check alert cycle
 */
function onLoad() {
    loadOrders();
    checkUpdates();
    checkAlerts();
}

document.addEventListener('DOMContentLoaded', onLoad);

/**
 * Closes the dropdown if the user clicks outside of it.
 */
window.onclick = function (event) {
    if (!event.target.matches('.dropbtn')) {
        var i;
        var dropdowns = document.getElementsByClassName("dropdown-content");
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}

/**
 * Checks for alerts from customers or kitchen staff.
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
 * Checks for database updates.
 */
function checkUpdates() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                if (asyncRequest.responseText == "true") {
                    loadOrders();
                }
                setTimeout(function () {
                    checkUpdates();
                }, 3000);
            }
        }, false);
        asyncRequest.open('GET', '/getorders?type=needsupdate', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: check orders");
    }
}

/**
 * Fills the order table with the orders.
 * @param {XMLHttpRequest} asyncRequest request containing the number of orders.
 */
function fillOrders(asyncRequest) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var size = parseInt(asyncRequest.responseText);
        document.getElementById("order_contents").innerHTML = "";
        var out = "";
        for (var i = 0; i < size; i++) {
            out += "<div id='item" + i + "' class='order box'>";
            out += "  <div id='table_number'>";
            out += "    <h2>Order " + i + "</h2>";
            out += "    <ul>";
            out += "    <p id='table_no" + i + "'></p>";
            out += "    <div id='item_names" + i + "'></div>";
            out += "    <p class='comment' id='comment" + i + "'></p>";
            out += "      </ul>";
            out += "      <p id='order_time'></p>";
            // out += "      <div class='paid_marker' style='background-color: green' id=" + i + "> $</div>"
            out += "      <div class='section dropdown'>";
            out += "        <div id='sw" + i + "' class='stopwatch'></div>";
            out += "        <div id='dropdown" + i + "' class='dropdown-content'>";
            out += "          <a id='order_btn" + i + "'  class='bar-item1 button'></a>";
            out += "          <a onclick='cancel(" + i + ")' class='bar-item3 button'>Cancel</a>";
            out += "        </div>";
            out += "        <p id='orderStatus" + i + "'></p>";
            out += "      </div>";
            out += "    </div>";
            out += "  </div>";
            out += "</div>";
            out += "<br>";
            getOrder("item_names", i, "itemnames");
            getOrder("table_no", i, "table", "Table no: ");
            getOrder("comment", i, "comment", "Comment:<br>");
            setOrderStatus(i);
        }
        document.getElementById("order_contents").innerHTML = out;
        for (var i = 0; i < size; i++) {
            stopwatches.push(new Stopwatch(document.querySelector('#sw' + i), document.querySelector('.results')));
            loadOrderStopwatch(i, "date");
        }
    }
}

/**
 * Make a get request which gets the value the order with
 * orderid was made and runs the setstopwatch function
 * @param {number} orderid id of the order
 * @param {type} parameter parameter from the database to be passed to the stopwatch.
 */
function loadOrderStopwatch(orderid, parameter) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            setStopwatch(asyncRequest, orderid);
        }, false);
        asyncRequest.open('GET', '/getorders?id=' + orderid + '&type=' + parameter, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert(exception.toString());
    }
}

/**
 * Sets the value of the stopwatch with the index orderid
 * @param {XMLHttpRequest} asyncRequest request of the time the order was made from the database.
 * @param {number} orderid index of the stopwatch you want to set
 */
function setStopwatch(asyncRequest, orderid) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var date = asyncRequest.responseText.split(":");
        stopwatches[orderid].setstart(date);
    }
}

/**
 * Opens the menu popup
 */
function openMenu() {
    document.getElementById('menu_popup').style.display = 'block';
    loadMenu();
}

/**
 * Removes item from the menu
 * @param {number} i index of the item to be removed
 */
function removeItem(i) {
    var element = document.getElementById("item" + i + "_content");
    element.parentNode.removeChild(element);
    this.size -= 1;
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getmenu?remove=' + i, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Send cancel order request");

    }
}

/**
 * Fills html menu with menu items from the database
 * @param {XMLHttpRequest} asyncRequest request for the menu size
 * @param {String} category of items to be put inside the menu (leave blank for all)
 */
function fillMenu(asyncRequest, category) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        this.size = parseInt(asyncRequest.responseText);
        var out = "";
        for (var i = 1; i <= this.size; i++) {
            out += '<li id="item' + i + '_content">';
            out += '    <h3 id="item_name' + i + '"></h3>';
            out += '    <button class="remove_btn" onclick="removeItem(' + i + ')">-</button>';
            out += '    <p id="item_desc' + i + '"></p>';
            out += '    <p id="item_price' + i + '"></p>';
            out += '</li>';
            out += '<hr>';
            getItem("item_name" + i, i, "name");
            getItem("item_desc" + i, i, "desc");
            getItem("item_price" + i, i, "price");
        }
        document.getElementById("menu_list").innerHTML = out;
    }
}

/**
 * Gets one parameter from one item in the database and stores it in the html tag id specified.
 * @param {number} id of the html tag
 * @param {number} itemid id of the item inside the database
 * @param {type} parameter name of the item inside the database
 */
function getItem(id, itemid, parameter) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            setHtml(asyncRequest, id);
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + itemid + '&type=' + parameter, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Item");
    }
}

/**
 * Creates a get request which gets the number of items in the database and runs
 * the function fillMenu once it receives a response.
 */
function loadMenu() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            fillMenu(asyncRequest);
        }, false);
        asyncRequest.open('GET', '/getmenu?type=count', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Menu");
    }
}

/**
 * Creates a get request which gets the number of orders in the database and runs
 * the function fillOrders once it receives a response.
 */
function loadOrders() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            fillOrders(asyncRequest);
        }, false);
        asyncRequest.open('GET', '/getorders?type=count', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Orders");
    }
}

/**
 * Gets one parameter from an order (specified with the order id) and stores it inside the "itemnames" div tag
 * @param {number} orderid the id of the order being fetched.
 * @param {type} parameter to be passed to the itemnames div tag
 */
function getOrder(id, orderid, parameter, extra) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.responseText != "" && asyncRequest.responseText != "0") {
                setHtml(asyncRequest, id + orderid, extra);
            }
        }, false);
        asyncRequest.open('GET', '/getorders?id=' + orderid + '&type=' + parameter, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Order");
    }
}

/**
 * Creates a get request to the server which gets the order status of each order,
 * and formats the order interface based upton the asnwer from the server.
 * @param {number} id the id of the order being updated.
 */
function setOrderStatus(id) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                if (asyncRequest.responseText == "Confirmed") {
                    document.getElementById("orderStatus" + id).innerHTML = "Order sent to kitchen.";
                    document.getElementById("item" + id).style.backgroundColor = "grey"; //order in kitchen
                    document.getElementById("order_btn" + id).style.display = "none";
                } else if (asyncRequest.responseText == "Cooked") {
                    document.getElementById("orderStatus" + id).innerHTML = "Order cooked.";
                    document.getElementById("item" + id).style.backgroundColor = "green"; //order ready  to be delivered
                    document.getElementById("order_btn" + id).onclick = function () {
                        delivered(id);
                    };
                    document.getElementById("order_btn" + id).innerText = "Delivered";

                } else if (asyncRequest.responseText == "Sent") {
                    document.getElementById("order_btn" + id).onclick = function () {
                        accept(id);
                    };
                    document.getElementById("order_btn" + id).innerText = "Accept";
                    var element = "Table no:<input id='tableno" + id + "' type='number' placeholder='0' style='width: 60px'>";
                    document.getElementById("orderStatus" + id).innerHTML = element;
                } else if (asyncRequest.responseText == "Delivered") {
                    document.getElementById("orderStatus" + id).innerHTML = "Waiting for customer to pay";
                    document.getElementById("order_btn" + id).style.display = "none";
                } else if (asyncRequest.responseText == "Paid") {
                }
            }
        }, false);
        asyncRequest.open('GET', '/getorders?id=' + id + '&type=status', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Order");
    }
}

/**
 * Fills the html tag with the specified id with the response text from the request
 * @param {XMLHttpRequest} asyncRequest which contains the output from the html request
 * @param {number} id of the html tag
 */
function setHtml(asyncRequest, id, extra) {
    if (extra == undefined) {
        extra = "";
    }
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        document.getElementById(id).innerHTML = extra + asyncRequest.responseText;
    }
}


/**
 * This function brings up a popup where the customer may enter their address and card details.
 * @param {number} i the order being checked out.
 */
function pay(i) {
    var out = "";
    out += "<div id = 'checkout_popup' class = modal>";
    out += "<form class='modal-content box'>";
    out += "<div class='imgcontainer'></div>";
    out += "<div class='popup_container'></div>";
    out += "<h2>Order Confirmation</h2>";
    out += "<div id='confirm_contents'>";
    out += "<h4 id='ctotal'>£0 Total</h4>";
    out += "</div>";
    out += "<div id='inputs'>";
    out += "<br> Name";
    out += "<p style='text-align: right; margin-top: -20px'>Table No.</p>";
    out += "<input style='width: 88%' type='text' placeholder='Name'>";
    out += "<input style='width:40px' type='text' placeholder='00' id='table-no' required style='color: black'>";
    out += "<br> Select payment type: <br>";
    out += "<select name='type'>";
    out += "<option value='Visa'>visa</option>";
    out += "<option value='MasterCard'>mastercard</option>";
    out += "<option value='Maestro'>maestro</option>";
    out += "<option value='VisaElectron'>visaelectron</option>";
    out += "</select>";
    out += "<br> Card Number: ";
    out += "<p style='text-align: right; margin-top: -20px'>Expiry Date:</p>";
    out += "<input style='width: 76%' type='text' name='cardnumber' placeholder='4444-3333-2222-1111' id='cardnumber'>";
    out += "<input type='text' name='month' placeholder='03' id='expmonth' class='cardExp'>";
    out += "<input type='text' name='year' placeholder='18' id='expyear' class='cardExp'>";
    out += "<br> Billing address:";
    out += "<input type='text' placeholder='Street'>";
    out += "<input style='width: 49.4%' type='text' placeholder='City'>";
    out += "<input style='width: 49.4%' type='text' placeholder='County'>";
    out += "<input style='width: 49.4%' type='text' placeholder='Postcode'>";
    out += "<input style='width: 49.4%' type='text' placeholder='Country'>";
    out += "<br> <br>";
    out += "<button type='button' onclick='cancelCheckout()'style='background-color: rgb(210, 255, 77)'>Cancel </button>";
    out += "<button id='checkout_btn' type='button' onclick='checkcreditcard(type.value,cardnumber.value,expmonth.value + '/' + expyear.value," + i + ")' style='background-color: rgb(210, 255, 77)'>Checkout </button>";
    out += "</div> </div> </form> </div>";
    document.innerHTML += out;
}

/**
 * @Author Sorin
 * Version 1.3
 * As of 12/03/2019
 * Checks card is valid.
 *
 * @param {string} cardname the type of card.
 * @param {string} cardnumber the numbers on the front of the card.
 * @param {string} expiry the expiry date of the card in the format: mm/yy
 * @param {number} order the order being checked out.
 */
function checkCreditCard(CardName, CardNumber, Expiry, order) {
    var valid = false;
    //removing spaces
    var cardnumber = CardNumber.replace(/\s/g, "").replace(/-/g, "");
    //making card name lowercase
    var cardname = CardName.toLowerCase();
    if (checkLength(cardnumber, cardname) & checkNumbers(cardnumber) & checkExpired(Expiry) & calculateCheckSum(cardnumber)) {
        if (cardname == "visa") {
            if (cardnumber.substring(0, 1) == '4') {
                valid = true;
            }
        } else if (cardname == "mastercard") {
            if (cardnumber.substring(0, 2) == '51' || cardnumber.substring(0, 2) == '52' || cardnumber.substring(0, 2) == '53' ||
                cardnumber.substring(0, 2) == '54' || cardnumber.substring(0, 2) == '55') {
                valid = true;
            }
        } else if (cardname == "maestro") {
            if (cardnumber.substring(0, 4) == '5018' || cardnumber.substring(0, 4) == '5020' || cardnumber.substring(0, 4) == '5038' ||
                cardnumber.substring(0, 4) == '6304' || cardnumber.substring(0, 4) == '6759' || cardnumber.substring(0, 4) == '6761' ||
                cardnumber.substring(0, 4) == '6762' || cardnumber.substring(0, 4) == '6763') {
                valid = true;
            }
        } else if (cardname == "visaelectron") {
            if (cardnumber.substring(0, 4) == '4026' || cardnumber.substring(0, 4) == '4508' || cardnumber.substring(0, 4) == '4844' ||
                cardnumber.substring(0, 4) == '4913' || cardnumber.substring(0, 4) == '4917' || cardnumber.substring(0, 6) == '417500') {
                valid = true;
            }
        }
    }
    if (valid) {
        checkout();
        paid(order);
    } else {
        window.alert("card details are not valid");
    }

}

/**
 * Checks that the length of the card number matches the required length for the type of card.
 * @param {String} cardnumber the 16 digit number on the front of the card.
 * @param {String} cardname the type of card e.g. Visa, Mastercard
 */
function checkLength(cardnumber, cardname) {
    if (cardname == "visa") {
        if (cardnumber.length == 16 || cardnumber.length == 13) {
            return true;
        }
    } else if (cardname == "mastercard" || cardname == "visaelectron") {
        if (cardnumber.length == 16) {
            return true;
        }
    } else if (cardname == "maestro") {
        if (cardnumber.length == 12 || cardnumber.length == 13 || cardnumber.length == 14 || cardnumber.length == 15 ||
            cardnumber.length == 16 || cardnumber.length == 18 || cardnumber.length == 19) {
            return true;
        }
    }
    return false;
}

/**
 * Checks that the card only contains numbers.
 * @param {String} cardnumber the 16 digit number on the front of the card.
 */
function checkNumbers(cardnumber) {
    var regex = /^[0-9]{13,19}$/;
    if (regex.exec(cardnumber)) {
        return true;
    }
    return false;
}

/**
 * Checks that the card is not expired.
 * @param {String} expiry expiry date on the front of the card.
 */
function checkExpired(expiry) {
    var currentdate = new Date();
    var year = currentdate.getFullYear();
    var month = currentdate.getMonth();
    year = parseInt(year.toString().substring(2, 4));
    var cardYear = parseInt(expiry.substring(3, 5));
    var cardMonth = parseInt(expiry.substring(0, 2));
    if (year < cardYear) {
        return true;
    } else if (year == cardYear) {
        if (month + 1 <= cardMonth) {
            return true;
        }
    }
    return false;
}

/**
 * Calculates the card's checksum.
 * @param {String} cardnumber the 16 digit number on the front of the card.
 */
function calculateCheckSum(cardnumber) {
    var sum = 0;
    var alternate = false;
    for (i = cardnumber.length - 1; i >= 0; i--) {
        var number = parseInt(cardnumber.substring(i, i + 1));
        if (alternate) {
            number *= 2;
            if (number > 9) {
                number = (number % 10) + 1;
            }
        }
        sum += number;
        alternate = !alternate;
    }
    return sum % 10 == 0;
}

/**
 * Closes the checkout window.
 */
function cancelCheckout() {
    document.getElementById('checkout_popup').style.display = 'none';
}

/**
 * Sets the dollar sign symbol to green and the order to paid in the database.
 * @param {number} i the order that has been paid for.
 */
function paid(i) {
    document.getElementById("orderStatus" + i).innerHTML = "Paid";
    document.getElementById("item" + i).style.backgroundColor = "yellow"; //order paid for
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id=' + i + '&type=paid', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Send paid order request");
    }
}

/**
 * Sets the order to accepted on screen and in the database.
 * @param {number} i the order being accepted.
 */
function accept(i) {
    if (document.getElementById("tableno" + i).value <= 0) {
        alert("Please insert a table number");
    } else {
        var tableno = document.getElementById("tableno" + i).value;
        var asyncRequest;
        try {
            asyncRequest = new XMLHttpRequest();
            asyncRequest.open('GET', '/getorders?id=' + i + '&type=accept&tableno=' + tableno, true);
            asyncRequest.send(null);
        } catch (exception) {
            alert("Request failed: Send accept order request" + exception.toString());
        }
        document.getElementById("orderStatus" + i).innerHTML = "Order sent to kitchen.";
        document.getElementById("item" + i).style.backgroundColor = "grey"; //order in kitchen
        document.getElementById("order_btn" + i).display = "none";
    }
}

/**
 * Sets the order to delivered on screen and in the database.
 * @param {number} i the order that has been delivered.
 */
function delivered(i) {
    document.getElementById("orderStatus" + i).innerHTML = "Order delivered.";
    document.getElementById("item" + i).style.backgroundColor = "green"; //order ready to be delivered
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id=' + i + '&type=delivered', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Send cancel order request");
    }
}

/**
 * Sets the order to cancelled on screen and in the database.
 * @param {number} i the order being cancelled.
 */
function cancel(i) {
    document.getElementById("orderStatus" + i).innerHTML = "Order cancelled.";
    document.getElementById("item" + i).style.backgroundColor = "lightgrey"; //order cancelled
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?id=' + i + '&type=cancel', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Send cancel order request");
    }
}

/**
 * retutns the value of a checkbox in the correct format
 * @param {type} cb is an input of type checkbox
 */
function getCbValue(cb) {
    if (cb.checked) {
        return "t";
    } else {
        return "f";
    }
}

/**
 * Adds a menu item to the menu and the database based on the values in the html inputs.
 */
function addMenuItem() {
    var out = "";
    out += "'" + document.getElementById("item_category").value + "',";
    out += "'" + document.getElementById("item_image").value + "',";
    out += "'" + document.getElementById("item_name").value + "',";
    out += "'" + document.getElementById("item_desc").value + "',";
    out += "'" + document.getElementById("item_price").value + "',";
    out += "'" + document.getElementById("item_calories").value + "',";
    out += "'" + getCbValue(document.getElementById("item_vegan")) + "',";
    out += "'" + getCbValue(document.getElementById("item_gluten")) + "',";
    out += "'" + getCbValue(document.getElementById("item_alcohol")) + "',";
    out += "'" + getCbValue(document.getElementById("item_hot")) + "'";
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getmenu?create=\'' + (this.size + 1) + '\',' + out, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Send cancel order request");
    }
    loadMenu();
}
