<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="static-fullscreen screen-door-filter"></div>
<div class="static-fullscreen flicker"></div>
<div class="cross-decoration left top"></div>
<div class="cross-decoration left bottom"></div>
<div class="cross-decoration right top"></div>
<div class="cross-decoration right bottom"></div>
<div class="static-fullscreen blur"></div>
<div class="static-fullscreen grey-cross-decoration-container">
    <%
        for (int it = 0; it < 15; ++it) {
            out.println("<div class=\"grey-cross-decoration\"></div>");
        }
    %>
</div>
<div class="logo-decoration">
    <img src="assets/mnhr.png" alt="mnhr-logo">
</div>
