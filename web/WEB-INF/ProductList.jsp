<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="eu_UK" />
<html>
<head>
    <title>Products</title>
    <link href="css/MainStyles.css" rel="stylesheet" type="text/css" />
</head>
<body>
<h3>Products</h3>
<table class="products">
    <tr><td>SKU</td><td>Name</td><td>Price</td><td>Amount</td><td>&nbsp;</td></tr>
    <c:forEach items="${productList}" var="product">
        <tr><td>${product.getId()}</td>
            <td><a href='<c:url value="/product/detail?id=${product.getId()}"></c:url>'>${product.getName()}</a></td>
            <td><fmt:formatNumber value="${product.getPrice()}" type="currency" currencyCode="EUR" minIntegerDigits="1" maxFractionDigits="2"></fmt:formatNumber></td>
            <td><fmt:formatNumber value="${product.getAmount()}" minIntegerDigits="1" maxFractionDigits="0" ></fmt:formatNumber></td>
            <td><a href='<c:url value="/product/edit?id=${product.getId()}"></c:url>'>Edit</a></td>
        </tr>
    </c:forEach>
</table>
sss
</body>
</html>
