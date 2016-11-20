<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
<h2>Error</h2>
<jsp:include page="Errors.jsp" />
<input type="button" value="Back" onclick="history.back()">
</body>
</html>
