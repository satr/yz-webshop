<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
    <link href="/css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="page">
    <%@include file="/Header.jsp" %>

    <h3>Welcome, <c:out value="${account.getFirstName()}"></c:out> !</h3>
    <div>
        <a href="/">Continue shopping!</a>
    </div>
</div>

<%@include file="/Footer.jsp" %>
</body>
</html>
