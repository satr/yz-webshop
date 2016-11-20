<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
    <title>Product</title>
    <link href="/css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="page">
    <%@include file="/Header.jsp" %>
    <c:set var="isEditAction" scope="request" value='${action == "edit"}'/>
    <h3><c:out value='${isEditAction ? "Edit Product" : "Add Product"}'></c:out></h3>
    <form action="/product/${action}" method="post">
        <div>
        <input type="hidden" name="id" value="${product.getId()}"/>
        <jsp:include page="../Errors.jsp" />
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
        <input type="button" value="Cancel" onclick="history.back()">
        </div>
    </form>
</div>
<%@include file="/Footer.jsp" %>
</body>
</html>
