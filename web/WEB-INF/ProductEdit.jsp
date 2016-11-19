<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
    <title>Product</title>
    <link href="/css/MainStyles.css" rel="stylesheet" type="text/css" />
</head>
<body>
<c:set var="isEditAction" scope="request" value='${action == "edit"}'/>
<h3><c:out value='${isEditAction ? "Edit Product" : "Add Product"}'></c:out></h3>
<form action="/product/${action}" method="post">
    <div>
    <table class="product">
        <c:if test='${isEditAction}' >
            <tr><td>SKU</td>
                <td>${product.getId()}</td>
            </tr>
        </c:if>
        <tr><td>Name</td>
            <td><input type="text" name="name" value="${product.getName()}" /></td>
        </tr>
        <tr><td>Price</td>
            <td><input type="text" name="price" value="${product.getPrice()}" /></td>
        </tr>
        <c:if test="${isEditAction}" >
            <tr><td>Amount</td>
                <td><input type="text" name="amount" value="${product.getAmount()}" /></td>
            </tr>
        </c:if>
    </table>
    </div>
    <br />
    <div>
    <input type="submit" value="Save">
    <input type="button" value="Cancel" onclick="window.location = '/products'">
    </div>
</form>
</body>
</html>
