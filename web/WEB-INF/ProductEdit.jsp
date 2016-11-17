<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
    <title>Product</title>
    <link href="${pageContext.request.contextPath}/css/MainStyles.css" rel="stylesheet" type="text/css" />
</head>
<body>
<form action="/product/${action}" method="post">
    <table class="product">
        <tr><td>Name</td>
            <td><input type="text" name="Name" value="${product.getName()}" /></td>
        </tr>
        <tr><td>Price</td>
            <td><input type="text" name="Price" value="${product.getPrice()}" /></td>
        </tr>
        <tr><td>Amount</td>
            <td><input type="text" name="Amount" value="${product.getAmount()}" /></td>
        </tr>
    </table>
    <input type="submit" value="Save">
    <input type="button" value="Cancel" onclick="history.back()">
</form>
</body>
</html>
