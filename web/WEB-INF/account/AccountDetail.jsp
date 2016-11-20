<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
    <link href="/css/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="page">
    <%@include file="/Header.jsp" %>

    <h3>Account</h3>
    <div>
        <form action="/account/edit" method="get">
            <div>
                <table class="account">
                    <tr><td>First Name</td>
                        <td>${account.getFirstName()}</td>
                    </tr>
                    <tr><td>Middle Name</td>
                        <td>${account.getMiddleName()}</td>
                    </tr>
                    <tr><td>Last Name</td>
                        <td>${account.getLastName()}</td>
                    </tr>
                    <tr><td>Email</td>
                        <td>${account.getEmail()}</td>
                    </tr>
                    <tr><td>Created</td>
                        <td>${account.getCreatedOn()}</td>
                    </tr>
                    <tr><td>Last Updated</td>
                        <td>${account.getUpdatedOn()}</td>
                    </tr>
                </table>
            </div>
            <br />
            <div>
                <input type="submit" value="Edit">
            </div>
        </form>
    </div>
</div>

<%@include file="/Footer.jsp" %>
</body>
</html>
