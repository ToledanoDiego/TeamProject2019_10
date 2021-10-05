/**
 * @author Ben Griffiths and Gunjan Sahityani and Shah Alam
 * @since: 27/03/2019
 */

//Stores items in basket
var items = Array();
var total = 0;

/**
 * Creates an item in the correct format
 * @param id of the item
 * @returns {{quantity: number, id: *}}
 */
function newItem(id) {
    return {
        'id': id,
        'quantity': 1
    }
}

/**
 * Ran when the document is loaded.
 */
function onLoad() {
    loadAllMenu();
    getOrderStatus();
    document.getElementById("defaultOpen").click();
}

document.addEventListener('DOMContentLoaded', onLoad);

/**
 * Get individual item elements and write response inside the tag with the id "id"
 * @param id of the html tag
 * @param itemid id of the item
 * @param parameter type of information to be retrieved from the server
 * @param category of the item
 * @param extra prefix to be added before the servers response to the html tag
 */
function getItemCategory(id, itemid, parameter, category, extra) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            setHtml(asyncRequest, id, extra);
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + itemid + '&type=' + parameter + '&category=' + category, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Item");
    }
}

/**
 * Makes a request to the server to call the waiter.
 */
function callWaiter() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/alerts?message=The customer needs help&role=Waiter', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Call Waiter" + exception.toString());
    }
}

/**
 * Get individual item elements and write response inside the tag with the id "id"
 * @param id of the html tag
 * @param itemid id of the item
 * @param parameter type of information to be retrieved from the server
 * @param extra prefix to be added before the servers response to the html tag
 */
function getItem(id, itemid, parameter, extra) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            setHtml(asyncRequest, id, extra);
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + itemid + '&type=' + parameter, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Item");
    }
}

/**
 * Makes a request to the server to login the user.
 */
function login() {
    var asyncRequest;
    var username = document.getElementById("user").value;
    var password = document.getElementById("pass").value;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            redirect_login(asyncRequest);
        }, false);
        asyncRequest.open('GET', '/login?username=' + username + '&password=' + password, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Item");
    }
}

/**
 * Redirects the user based on the users role in the login system.
 * @param asyncRequest contains the response from the server.
 */
function redirect_login(asyncRequest) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var role = asyncRequest.responseText;
        alert(role);
        if (role == "Waiter") {
            location.replace("/waiter");
        } else if (role == "KitchenStaff") {
            location.replace("/kitchenstaff");
        } else if (role == "Admin") {
        }
    }
}

/**
 * Makes a request to the server to create an order with the customers items.
 */
function checkout() {
    var stritems = "";
    var first = -1;
    for (var i = 0; i < items.length; i++) {
        if (items[i] != null) {
            for (var j = 0; j < items[i]['quantity']; j++) {
                if (first == -1) {
                    first = i;
                } else {
                    stritems += ",";
                }
                stritems += items[i]['id'];
            }
        }
    }
    var comment = document.getElementById("comment").value;
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?type=create&items=' + stritems + '&comment=' + comment, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Make order");
    }
    document.getElementById('checkout_popup').style.display = 'none';
    document.getElementById('placed').className = "active";
}

/**
 * Fills the menu with the menu items from the server.
 * @param asyncRequest contains the response from the server.
 * @param category of menu item to be filled.
 */
function fillMenu(asyncRequest, category) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var size = parseInt(asyncRequest.responseText);
        var out = document.getElementById(category + "_contents").innerHTML;
        for (var i = 0; i < size; i++) {
            out += "<div class='item'>";
            out += "  <h2 id='" + category + "_name" + i + "' class='item_text'></h2>";
            out += "  <img class='item-img' id ='" + category + "_image" + i + "' src='https:/i.ibb.co/GnmmtjX/jalapeno-poppers-11.jpg'>";
            out += "  <div id='" + category + "_popup" + i + "' class=' desc-popup animate box'>";
            out += "    <button  onclick='closeDescPopup(\"" + category + "\", " + i + ")' style='float:right'> X </button>";
            out += "    <p id='" + category + "_desc" + i + "' style=\"text-align:center\"></p>";
            out += "    <p id='" + category + "_allergies" + i + "' style='text-align:center; font-weight:bold'></p>";
            out += "    <p id='" + category + "_calories" + i + "' style='text-align:center; font-weight:bold'></p>";
            out += "  </div>";
            out += "  <p id='" + category + "_price" + i + "' style='text-align:center; font-weight:bold' ></p>";
            out += "  <button id='" + category + "_additem" + i + "' class='add_button'>add</button>";
            out += "  <button id=\"desc\" class='more_info' onclick='openDescPopup(\"" + category + "\", " + i + ")'>?</button>";
            out += "</div>";
            getItemCategory(category + "_name" + i, i, "name", category);
            getItemCategory(category + "_desc" + i, i, "desc", category);
            getItemCategory(category + "_price" + i, i, "price", category, "£");
            getItemCategory(category + "_allergies" + i, i, "allergies", category, "Allergies: ");
            getItemCategory(category + "_calories" + i, i, "calories", category, "Calories: ");
        }
        document.getElementById(category + "_contents").innerHTML = out;
        for (var i = 0; i < size; i++) {
            setAddButton(category + "_additem" + i, i, category);
            setImage(category + "_image" + i, i, category);
        }
    }
}

/**
 * Opens the description popup for an item.
 * @param category of the item to be viewed.
 * @param id of the item to be view.
 */
function openDescPopup(category, id) {
    document.getElementById(category + "_image" + id).style.display = "none";
    document.getElementById(category + "_popup" + id).style.display = 'block';
}

/**
 * Closes the description popup for an item.
 * @param category of the item to be closed.
 * @param id of the item to be close.
 */
function closeDescPopup(category, id) {
    document.getElementById(category + "_popup" + id).style.display = 'none';
    document.getElementById(category + "_image" + id).style.display = "block";
}

/**
 * Sets the add button for each item to the correct item id.
 * @param id of the add button
 * @param itemid id of the item
 * @param category of the item
 */
function setAddButton(id, itemid, category) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                document.getElementById(id).onclick = function () {
                    addItem(parseInt(asyncRequest.responseText));
                };
            }
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + itemid + '&type=id&category=' + category, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Item");
    }
}

/**
 * Sets the image for each item.
 * @param id of the image tag.
 * @param itemid id of the item
 * @param category of the item
 */
function setImage(id, itemid, category) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                document.getElementById(id).setAttribute("src", asyncRequest.responseText);
            }
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + itemid + '&type=image&category=' + category, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Get Item");
    }
}

/**
 * Gets the amount of menu items in the menu.
 * @param category of the items.
 */
function loadMenu(category) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            fillMenu(asyncRequest, category);
        }, false);
        asyncRequest.open('GET', '/getmenu?type=count&category=' + category, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Menu " + category);
    }
}

/**
 * loads all item categories.
 */
function loadAllMenu() {
    loadMenu("starter");
    loadMenu("main");
    loadMenu("dezert");
    loadMenu("dips");
    loadMenu("drink");
}

/**
 * Sets the html inside the element "id" to the response from the request
 * @param asyncRequest contains teh response from the server.
 * @param id of the html tag/
 * @param extra prefix to be added before the server response.
 */
function setHtml(asyncRequest, id, extra) {
    if (extra == undefined) {
        extra = "";
    }
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        document.getElementById(id).innerHTML = extra + asyncRequest.responseText;
    }
}

//Code for login popup
var modal = document.getElementById('login_popup');
window.onclick = function (event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

/**
 * Adds item to the end of the basket.
 * @param i index of the item inside the checkout array.
 */
function updateCheckout(i) {
    var id = items[i]['id'];
    var out = document.getElementById("checkout_contents").innerHTML;
    out += "<div id='item_container" + i + "'>";
    out += "<p class='citem' id='citem" + i + "'>loading</p>";
    out += "<p class='item_price' id='cprice" + i + "'></p>";
    out += "<button type='button' class='remove box' onclick='removeItem(" + i + ")'>X</button>";
    out += "<p class='cquant' id='cquant" + i + "'>1</p>"
    out += "</div>";
    getItem("citem" + i, id, "name");
    getItem("cprice" + i, id, "price", " - £");
    document.getElementById("checkout_contents").innerHTML = out;
}

/**
 * remove element from checkout and list
 * @param i index of the item inside the checkout array.
 */
function removeItem(i) {
    removeFromTotal(items[i]['id']);
    items[i]['quantity']--;
    document.getElementById("cquant" + i).innerHTML = items[i]['quantity'];
    if (items[i]['quantity'] == 0) {
        var element = document.getElementById("item_container" + i);
        element.parentNode.removeChild(element);
        var element = document.getElementById("citem_container" + i);
        element.parentNode.removeChild(element);
        // items.splice(i, 1);
        items[i] = null;
    }
}


/**
 * adds item to the basket and list
 * @param id of the item
 */
function addItem(id) {
    for (var i = 0; i < items.length; i++) {
        if ((items[i] != null) && (items[i]['id'] == id)) {
            items[i]['quantity']++;
            break;
        }
    }
    if (i == items.length) {
        items.push(newItem(id));
        updateCheckout(items.length - 1);
        updateConfirm(items.length - 1);
        updatePayment(items.length - 1);
    } else {
        document.getElementById("cquant" + i).innerHTML = items[i]['quantity'];
    }
    addToTotal(id);

}

/**
 * Gets price of item from server and adds it to the total.
 * @param id of the item.
 */
function addToTotal(id) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                total = total + parseFloat(asyncRequest.responseText);
                document.getElementById("total").innerHTML = "Total £" + total;
                document.getElementById("ctotal").innerHTML = "Total £" + total;
                document.getElementById("ptotal").innerHTML = "Total £" + total;
            }
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + id + '&type=price', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Menu " + category);
    }
}

/**
 * Close the checkout popup
 */
function cancelCheckout() {
    document.getElementById('checkout_popup').style.display = 'none';
}

/**
 * Add items to the confirm popup
 * @param i index of the item inside the checkout array.
 */
function updateConfirm(i) {
    var id = items[i]['id'];
    var out = document.getElementById("confirm_contents").innerHTML;
    out += "<div id='citem_container" + i + "'>";
    out += "<p class='citem' id='c2item" + i + "'>loading</p>";
    out += "<p class='item_price' id='c2price" + i + "'>0</p>";
    out += "</div>";
    getItem("c2item" + i, id, "name");
    getItem("c2price" + i, id, "price");
    document.getElementById("confirm_contents").innerHTML = out;
}

/**
 * Add items to the payment popup
 * @param i index of the item inside the checkout array.
 */
function updatePayment(i) {
    var id = items[i]['id'];
    var out = document.getElementById("payment_contents").innerHTML;
    out += "<div id='pitem_container" + i + "'>";
    out += "<p class='citem' id='pitem" + i + "'>loading</p>";
    out += "<p class='item_price' id='pprice" + i + "'>0</p>";
    out += "</div>";
    getItem("pitem" + i, id, "name");
    getItem("pprice" + i, id, "price");
    document.getElementById("payment_contents").innerHTML = out;
}

/**
 * Opens category of items inside the menu.
 * @param evt
 * @param Category of the items
 */
function openCategory(evt, Category) {
    var i, tabcontent, tablinks;

    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(Category).style.display = "block";
    evt.currentTarget.className += " active";
}

/**
 * Gets price of item from server and subtracts it from the total.
 * @param id of the item.
 */
function removeFromTotal(id) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                total = total - parseFloat(asyncRequest.responseText);
                document.getElementById("total").innerHTML = "Total £" + total;
                document.getElementById("ctotal").innerHTML = "Total £" + total;
                document.getElementById("ptotal").innerHTML = "Total £" + total;
            }
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + id + '&type=price', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Menu " + category);
    }
}

/**
 * Opens payment popup
 */
function openPayment() {
    document.getElementById('payment_popup').style.display = "block";
}

/**
 * Closes payment popup
 */
function cancelPayment() {
    document.getElementById('payment_popup').style.display = "none";
}

/**
 * Sends a request to pay for the customers order to the server.
 */
function pay() {
    var type = document.getElementById("type");
    var cardnumber = document.getElementById("cardnumber");
    var expmonth = document.getElementById("expmonth");
    var expyear = document.getElementById("expyear");

    if (checkCreditCard(type.value, cardnumber.value, expmonth.value + '/' + expyear.value, " + i + ")) {
        var asyncRequest;
        try {
            asyncRequest = new XMLHttpRequest();
            asyncRequest.open('GET', '/getorders?type=pay', true);
            asyncRequest.send(null);
        } catch (exception) {
            alert("Request failed: set Order Status Paid");
        }
        document.getElementById('payment_popup').style.display = "none";
    } else {
        alert("Invalid details");
    }
}


/**
 * Sends a request to get the customers order status from the server.
 */
function getOrderStatus() {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                document.getElementById("pay-button").style.display = "none";
                if (asyncRequest.responseText == "Sent") {
                    document.getElementById('placed').className = "active";
                    document.getElementById('cooking').className = "";
                    document.getElementById('cooked').className = "";
                    document.getElementById('delivered').className = "";
                } else if (asyncRequest.responseText == "Confirmed") {
                    document.getElementById('placed').className = "active";
                    document.getElementById('cooking').className = "active";
                    document.getElementById('cooked').className = "";
                    document.getElementById('delivered').className = "";
                } else if (asyncRequest.responseText == "Cooked") {
                    document.getElementById('placed').className = "active";
                    document.getElementById('cooking').className = "active";
                    document.getElementById('cooked').className = "active";
                    document.getElementById('delivered').className = "";
                } else if (asyncRequest.responseText == "Delivered") {
                    document.getElementById('placed').className = "active";
                    document.getElementById('cooking').className = "active";
                    document.getElementById('cooked').className = "active";
                    document.getElementById('delivered').className = "active";
                    document.getElementById("pay-button").style.display = "block";
                } else {
                    document.getElementById('placed').className = "";
                    document.getElementById('cooking').className = "";
                    document.getElementById('cooked').className = "";
                    document.getElementById('delivered').className = "";
                }
                setTimeout(function () {
                    getOrderStatus();
                }, 3000);
            }

        }, false);
        asyncRequest.open('GET', '/getorders?', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: get Order Status");
    }
}