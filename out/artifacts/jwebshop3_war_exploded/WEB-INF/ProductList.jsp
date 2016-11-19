<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            <td class="num"><fmt:formatNumber minIntegerDigits="1" maxFractionDigits="2" minFractionDigits="2" >${product.getPrice()}</fmt:formatNumber></td>
            <td class="num"><fmt:formatNumber value="${product.getAmount()}" minIntegerDigits="1" maxFractionDigits="0" ></fmt:formatNumber></td>
            <td><a href='<c:url value="/product/edit?id=${product.getId()}"></c:url>'>Edit</a></td>
        </tr>
    </c:forEach>
</table>
<a href="/product/add">Add</a>&nbsp;<a href="/products">Refresh</a>
</body>
</html>
