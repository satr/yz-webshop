<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
<h2>Error</h2>
<h4><c:forEach items="${messages}" var="message">
    <c:out value="${message}"></c:out><br />
</c:forEach></h4>
<a href="#" onclick="history.back()"><< Back</a>
</body>
</html>