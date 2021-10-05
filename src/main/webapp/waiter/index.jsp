<%@ page import="servlets.Servlet" %>

<html lang="">
<head>
    <title></title>
    <% Servlet waiter_servlet = new Servlet(request);
        if (!waiter_servlet.isLoggedIn()) {
            out.write("Not logged in");
            return;
        } %>
    <link rel="stylesheet" href="index.css"/>
    <link rel="stylesheet" href="haspaid.css"/>
</head>
<body>

<div id="container">
    <div id="title" class="box">
        <h1>Orders</h1>
    </div>

    <button id="viewMenu" onClick="openMenu()"> View/Edit Menu</button>

    <br>
    <!-- ORDERS -->
    <div id="order_contents"></div>

</div>

<div id="menu_popup" class="modal">
    <form class="modal-content animate box">
        <button style="float:right" id="hide-popup"
                onclick="document.getElementById('menu_popup').style.display='none'"> X
        </button>


        <ul id="menu_list"></ul>

        <h3> Add Item </h3>
        <!-- <label for="item_name"> <b> Name: </b> </label> -->
        <input class="input_text" type="text" placeholder="Item Category" id="item_category">
        <input class="input_text" type="text" placeholder="Item Image URL" id="item_image">
        <input class="input_text" type="text" placeholder="Item Name" id="item_name">
        <input class="input_text" type="text" placeholder="Item Description" id="item_desc">
        <label for="item_price">Price</label>
        <input class="input_num" type="number" placeholder="0" id="item_price">
        <label for="item_calories">Calories</label>
        <input class="input_num" type="number" placeholder="0" id="item_calories">
        <label for="item_vegan">Vegan</label>
        <input type="checkbox" id="item_vegan">
        <label for="item_gluten">Gluten</label>
        <input type="checkbox" id="item_gluten">
        <label for="item_alcohol">Alcohol</label>
        <input type="checkbox" id="item_alcohol">
        <label for="item_hot">Hot</label>
        <input type="checkbox" id="item_hot">
        <br>
        <button onclick="addMenuItem()" style="color:rgb(77, 46, 0); background-color:rgb(210, 255, 77)"> Add Item
        </button>
    </form>
</div>

<script src="../stopwatch.js"></script>
<script src="index.js"></script>

</body>
</html>