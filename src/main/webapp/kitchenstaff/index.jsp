<%@ page import="servlets.Servlet" %>


<html>

<head>
    <% Servlet servlet = new Servlet(request);
        if (!servlet.isLoggedIn()) {
            out.write("Not logged in");
            return;
        }%>
    <link rel="stylesheet" href="index.css">
</head>

<body>
<div id="container">

    <div id="title">
        <h1>Orders</h1>
    </div>

    <div id="order_contents"></div>


    <button type="button" class="box item" onclick="callWaiter()"
            style="position: absolute; top: 15px; right: 240px; width: 80px; height: 80px;">
        <div class>
            <h4>Help</h4>
        </div>
    </button>

    </script>
    <script src="stopwatch.js"></script>
    <script src="index.js"></script>


</body>

</html>
