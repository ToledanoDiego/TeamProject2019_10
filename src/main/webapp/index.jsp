<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>

<html>

<head>
    <link rel="stylesheet" type="text/css" href="index.css">
    <img id="logo" src="images/oaxaca.jpg" width="500" height="100">
    <meta charset="utf-8">
    <title>Menu</title>
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
</head>

<body>
<div class="container">
    <div class="panel-heading">
        <h2><a style="color:rgb(77, 46, 0); cursor:pointer" onclick="openCategory(event, 'Starter')"
               id="defaultOpen">Starters</a></h2>
        <h2><a style="color:rgb(77, 46, 0); cursor:pointer" onclick="openCategory(event, 'Main')">Mains</a></h2>
        <h2><a style="color:rgb(77, 46, 0); cursor:pointer" onclick="openCategory(event, 'Dezert')">Desserts</a>
        </h2>
        <h2><a style="color:rgb(77, 46, 0); cursor:pointer" onclick="openCategory(event, 'Dips')">Dips</a></h2>
        <h2><a style="color:rgb(77, 46, 0); cursor:pointer" onclick="openCategory(event, 'Drink')">Drinks</a></h2>
    </div>

    <div class="categories">
        <div class="panel-title">
            <div id="Starter" class="tabcontent">
                <div id="starter_contents">
                    <h2>Starters</h2>
                </div>
            </div>
        </div>

        <div class="panel-title">
            <div id="Main" class="tabcontent">
                <div id="main_contents">
                    <h2>Mains</h2>
                </div>
            </div>
        </div>

        <div class="panel-title">
            <div id="Dezert" class="tabcontent">
                <div id="dezert_contents">
                    <h2>Desserts</h2>
                </div>
            </div>
        </div>

        <div class="panel-title">
            <div id="Dips" class="tabcontent">
                <div id="dips_contents">
                    <h2>Dips</h2>
                </div>
            </div>
        </div>

        <div class="panel-title">
            <div id="Drink" class="tabcontent">
                <div id="drink_contents">
                    <h2>Drinks</h2>
                </div>
            </div>
        </div>
    </div>

    <!-- CHECKOUT POPUP -->
    <div id="checkout_popup" class="Cmodal">
        <form class="Cmodal-content box">
            <div class="Cpopup_container">
                <h2 style="text-align: center">Order Confirmation</h2>
                <div id="confirm_contents">

                    <h4 type="number" id="ctotal">£0 Total</h4>
                    <br>
                </div>
                <div id="inputs">
                    <p style="margin-left:25%"> Comments: </p>
                    <textarea rows="3" id="comment" name="comment" style="width:85%"
                              placeholder="Anything you'd like to add?"> </textarea>
                    <br>
                    <button type="button" onclick="cancelCheckout()"
                            style="background-color: rgb(210, 255, 77)">Cancel
                    </button>
                    <button class="checkout_btn" type="button" onclick="checkout()"
                            style="background-color: rgb(210, 255, 77)">Checkout
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>


<!-- PAYMENT POPUP -->
<div id="payment_popup" class="pmodal">
    <form class="Cmodal-content box">
        <div class="Cpopup_container">
            <h2 style="text-align: center">Your Bill</h2>
            <div id="payment_contents"></div>
            <h4 id="ptotal">£0 Total</h4>
            <br>
            <label id="ctiplabel" for="ctip">Tip £</label>
            <input id="ctip" style="width: 10%" type="number" min="0.01" step="0.01" max="10000" value="0.00"
                   float="left">
        </div>
        <div id="inputs">
            Name:
            <input style="width: 88%" type="text" placeholder="Name">

            <br>
            Select a payment type:
            <br>
            <select name="type" id="type">
                <option value="Visa">visa</option>
                <option value="MasterCard">mastercard</option>
                <option value="Maestro">maestro</option>
                <option value="VisaElectron">visaelectron</option>
            </select>
            <br>
            Card number:
            <p style="text-align: right; margin-top: -20px">Expiry Date:</p>
            <input style="width: 76%" type="text" name="cardnumber" placeholder="4444-3333-2222-1111"
                   id="cardnumber">
            <input type="text" name="month" placeholder="03" id="expmonth" class="cardExp">
            <input type="text" name="year" placeholder="18" id="expyear" class="cardExp">
            <br>
            Billing Address
            <input type="text" placeholder="Street">
            <input style="width: 49.4%" type="text" placeholder="City">
            <input style="width: 49.4%" type="text" placeholder="County">
            <input style="width: 49.4%" type="text" placeholder="Postcode">
            <input style="width: 49.4%" type="text" placeholder="Country">
            <br>
            <br>
            <button type="button" onclick="cancelPayment()"
                    style="background-color: rgb(210, 255, 77)">Cancel
            </button>
            <button class="checkout_btn" type="button" onclick="pay()"
                    style="background-color: rgb(210, 255, 77)">Pay
            </button>
        </div>
    </form>
</div>


<div class="footer">
    <p>team10@restaurant.com</p>
    <p>Egham Hill, Egham TW20 0EX 0208 9991234</p>
    <p>
        |
        <button id=stafflogin
                onclick="document.getElementById('login_popup').style.display='block'">
            Staff Login
        </button>
        |
    </p>
</div>

<div id="checkout">
    <button type="button" id="waiter-btn" onclick="callWaiter()">Call waiter</button>
    <h3>Checkout Basket</h3>
    <div id="checkout_contents">

        <div id="item_container">
        </div>

    </div>
    <h2 id="total" style="float: right; margin-right: 10px; margin-top: 5px">Total £0</h2>
    <br>
    <button style="margin-left: 5vw"
            onclick="document.getElementById('checkout_popup').style.display='block'"
            style="background-color:rgb(210, 255, 77)">Checkout
    </button>
</div>

<!-- LOGIN POPUP -->
<div id="login_popup" class="modal">
    <form class="modal-content animate box" action="/action_page.php">
        <div class="popup_container">
            <label for="user"> <b> Username </b>
            </label> <input type="text" placeholder="Enter Username" id="user">
            <label for="pass"> <b> Password </b>
            </label> <input type="password" placeholder="Enter Password" id="pass"
                            required>
            <br></br>
            <button type="button" onclick="login()">Login</button>

            <input type="checkbox" checked="checked" name="remember">
            Remember me

            <button type="button"
                    onclick="document.getElementById('login_popup').style.display='none'"
                    style="background-color: rgb(210, 255, 77)">Cancel
            </button>
            <span class="pass">Forgot <a href="#">password?</a></span>

        </div>
    </form>
</div>

<div class="progress-container">
    <p style="text-align:center"> Your order is now: </p>
    <ul class="progressbar">
        <li id="placed"><img src="images/waiter.png"> placed</li>
        <li id="cooking"><img src="images/cooking.png"> cooking</li>
        <li id="cooked"><img src="images/taco.png"> cooked</li>
        <li id="delivered"><img src="images/meal.png"> delivered</li>
    </ul>
</div>

<div class="pay-container ">
    <button id="pay-button" class="pay-button" onclick="openPayment()"><img src="images/pay.png"> Pay</button>
</div>

<script src="Payment/checkcreditcard.js"></script>
<script src="index.js"></script>
</body>

</html>
