<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Product Detail</title>
</head>
<body>
<h3><c:out value="${product.getName()}"></c:out></h3>
<table border="1" width="1">
    <tr><td>Price</td>
        <td><fmt:formatNumber value="${product.getPrice()}" currencyCode="£" minIntegerDigits="1" maxFractionDigits="2"></fmt:formatNumber></td>
    </tr>
    <tr><td>Amount</td>
        <td><fmt:formatNumber value="${product.getAmount()}" minIntegerDigits="1" maxFractionDigits="0" currencyCode=""></fmt:formatNumber></td>
    </tr>
</table>
<a href="#" onclick="history.back()"><< Back</a>
</body>
</html>
