<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Web lab 2</title>
        <meta charset="UTF-8">
        <link rel="icon" href="assets/logo.ico">
        <link rel="stylesheet" href="style/filter.css">
        <link rel="stylesheet" href="style/index.css">
        <script defer src="script/index.js"></script>
        <script defer src="script/canvas.js"></script>
        <script defer src="script/controller.js"></script>
    </head>
    <body>
        <jsp:include page="components/filter.jsp" />
        <jsp:include page="components/modal/modal.jsp" />
        <jsp:include page="components/header.jsp" />
        <jsp:include page="components/main/main.jsp" />
    </body>
</html>