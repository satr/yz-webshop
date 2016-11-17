<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Product</title>
    <style>
        tr {border-style: solid;border-width: 1px; border-top: solid}
    </style>
</head>
<body>
<h3>Products</h3>
<table style="border: 1px solid;">
    <tr><td>SKU</td><td>Name</td><td>Price</td></tr>
    <c:forEach items="${productList}" var="product">
        <tr><td>${product.getId()}</td>
            <td><a href='<c:url value="/product/detail?id=${product.getId()}"></c:url>'>${product.getName()}</a></td>
            <td><fmt:formatNumber value="${product.getAmount()}" minIntegerDigits="1" maxFractionDigits="0" currencyCode=""></fmt:formatNumber></td></tr>
    </c:forEach>
</table>
</body>
</html>
