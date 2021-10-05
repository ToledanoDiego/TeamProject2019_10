//Stores items in basket
var items = Array();
var total = 0;

function newItem(id) {
    return {
        'id': id,
        'quantity': 0
    }
}

function onLoad() {
    loadAllMenu();
    document.getElementById("defaultOpen").click();
}

document.addEventListener('DOMContentLoaded', onLoad);

//Get indiviual item elements and write response inside the tage with the id "id"
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

function checkout() {
     document.getElementById('checkout_popup').style.display = 'none';
     document.getElementById('placed').className = "active";

    var stritems = "";
    var first = -1;
    for (var i = 0; i < items.length; i++) {
        if (items[i] != null) {
            if (first == -1) {
                first = i;
            } else {
                stritems += ",";
            }
            stritems += items[i];
        }
    }
    var tip = document.getElementById("ctip").value;

    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.open('GET', '/getorders?type=create&items=' + stritems + "&tip=" + tip, true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Make order");
    }
    document.getElementById('checkout_popup').style.display = 'none';
    document.getElementById('placed').className = "active";
}

function fillMenu(asyncRequest, category) {
    if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
        var size = parseInt(asyncRequest.responseText);
        var out = document.getElementById(category + "_contents").innerHTML;
        for (var i = 0; i < size; i++) {
            out += "<div class='item'>";
            out += "  <div class='item_text'>";
            out += "    <h2 id='" + category + "_name" + i + "'></h2>";
            out += "    <img id ='" + category + "_image" + i + "' src='https:/i.ibb.co/GnmmtjX/jalapeno-poppers-11.jpg' width=150px height=150px padding-right=25px>";
            out += "    <p id='" + category + "_price" + i + "' style='text-align:center; font-weight:bold' > ";
            out += "    </p> "
            out += "    <button id='" + category + "_additem" + i + "' class='add_button'>add</button>";
            out += "    <button id=\"desc\" class='more_info' onclick='openDescPopup(\"" + category + "\", " + i + ")'>?</button>";
            out += "  </div>";
            out += "</div>";
            out += "<div id='" + category + "_popup" + i + "' class='desc-popup modal-content animate box'>";
            out += "    <button onclick='closeDescPopup(\"" + category + "\", " + i + ")' style='float:right'> x </button>";
            out += "    <p id='" + category + "_desc" + i + "' style=\"text-align:center\"></p>";
            out += "    <p id='" + category + "_allergies" + i + "' style='text-align:center'></p>";
            out += "    <p id='" + category + "_calories" + i + "' style='text-align:center; font-weight:bold'></p>";
            out += "</div>";
            getItemCategory(category + "_name" + i, i, "name", category);
            getItemCategory(category + "_desc" + i, i, "desc", category);
            getItemCategory(category + "_price" + i, i, "price", category, "£");
            // getItemCategory(category + "_allergies" + i, i, "allergies", category);
            getItemCategory(category + "_calories" + i, i, "calories", category, "Calories: ");
        }
        document.getElementById(category + "_contents").innerHTML = out;
        for (var i = 0; i < size; i++) {
            setAddButton(category + "_additem" + i, i, category);
            setImage(category + "_image" + i, i, category);
        }
    }
}

function openDescPopup(category, id) {
    document.getElementById(category + "_popup" + id).style.display = 'block';
}

function closeDescPopup(category, id) {
    document.getElementById(category + "_popup" + id).style.display = 'none';
}


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

function loadAllMenu() {
    loadMenu("starter");
    loadMenu("main");
    loadMenu("dezert");
    loadMenu("dips");
    loadMenu("drink");
}

//Sets the html inside the element "id" to the response from the request
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

//adds last item to the end of the basket
function updateCheckout(i) {
    var id = items[i];
    var out = document.getElementById("checkout_contents").innerHTML;
    out += "<div id='item_container" + i + "'>";
    out += "<button type='button' class='box' onclick='removeItem(" + i + ")'>X</button>";
    out += "<p class='citem' id='citem" + i + "'>loading</p>";
    out += "<p class='item_price' id='cprice" + i + "'>0</p>";
    out += "</div>";
    getItem("citem" + i, id, "name");
    getItem("cprice" + i, id, "price");
    document.getElementById("checkout_contents").innerHTML = out;
}

//remove element from checkout and list
function removeItem(i) {
    removeFromTotal(items[i]);
    items.splice(i, 1);
    var element = document.getElementById("item_container" + i);
    element.parentNode.removeChild(element);
    var element = document.getElementById("citem_container" + i);
    element.parentNode.removeChild(element);

}

//adds item to the basket and list
function addItem(id) {
//     for (var item : items) {
//
//     }

    items.push(id);
    addToTotal(id);
    updateCheckout(items.length - 1);
    updateConfirm(items.length - 1);
}

function removeFromTotal(id) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                total = total - parseFloat(asyncRequest.responseText);
                document.getElementById("total").innerHTML = "Total £" + total;
                document.getElementById("ctotal").innerHTML = "Total £" + total;
            }
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + id + '&type=price', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Menu " + category);
    }
}

function addToTotal(id) {
    var asyncRequest;
    try {
        asyncRequest = new XMLHttpRequest();
        asyncRequest.addEventListener("readystatechange", function () {
            if (asyncRequest.readyState == 4 && asyncRequest.status == 200) {
                total = total + parseFloat(asyncRequest.responseText);
                document.getElementById("total").innerHTML = "Total £" + total;
                document.getElementById("ctotal").innerHTML = "Total £" + total;
            }
        }, false);
        asyncRequest.open('GET', '/getmenu?id=' + id + '&type=price', true);
        asyncRequest.send(null);
    } catch (exception) {
        alert("Request failed: Load Menu " + category);
    }
}

function cancelCheckout() {
    document.getElementById('payment_popup').style.display = 'none';
}

function updateConfirm(i) {
    var id = items[i];
    var out = document.getElementById("confirm_contents").innerHTML;
    out += "<div id='citem_container" + i + "'>";
    out += "<p class='citem' id='c2item" + i + "'>loading</p>";
    out += "<p class='item_price' id='c2price" + i + "'>0</p>";
    out += "</div>";
    getItem("c2item" + i, id, "name");
    getItem("c2price" + i, id, "price");
    document.getElementById("confirm_contents").innerHTML = out;
}

function checkState(){
  //pull state of order from db and check if 'confirmed'
  document.getElementById("cooking").className = "active";

  //pull state of order from db and check if 'cooked'
  document.getElementById("cooked").className = "active";

  //pull state of order from db and check if 'delivered'
  document.getElementById("delivered").className = "active";

  // if(document.getElementById("delivered").className == "active"){
  //   setTimeout('', 15000); //wait 15 seconds before showing pay button
  //   document.getElementById('progress-container').style.display = "none";
  //   document.getElementById('pay-container').style.display = "block";
  // }
}


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

function pay(){
  document.getElementById('payment_popup').style.display = "none";
}
