<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Product Detail</title>
    <link href="/css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="page">
<%@include file="/Header.jsp" %>

<h3>Product</h3>
<div>
<table class="product">
    <tr><td>SKU</td>
        <td>${product.getId()}</td>
    </tr>
    <tr><td>Name</td>
        <td>${product.getName()}</td>
    </tr>
    <tr><td>Price</td>
        <td><fmt:formatNumber minIntegerDigits="1" maxFractionDigits="2" minFractionDigits="2" >${product.getPrice()}</fmt:formatNumber></td>
    </tr>
    <tr><td>Amount</td>
        <td><fmt:formatNumber value="${product.getAmount()}" minIntegerDigits="1" maxFractionDigits="0" currencyCode=""></fmt:formatNumber></td>
    </tr>
</table>
</div>
<br />
<div>
    <input type="button" value="Back" onclick="window.location = '/products'">
</div>
</div>

<%@include file="/Footer.jsp" %>
</body>
</html>
