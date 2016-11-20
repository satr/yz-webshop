<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="top-menu">
    <table style="width: 100%">
        <tr>
            <td>
                <ul class="top-menu">
                    <li><a href="/">Home</a></li>
                    <li><a href="/products">Products</a></li>
                </ul>
            </td>
            <td width="130px">
                <ul class="top-menu">
                    <c:choose>
                        <c:when test="${account != null && account.getId() != 0}">
                            <li>
                                Hello, <a href="/account/detail"><c:out value="${account.getFirstName()}"></c:out></a>
                            </li>
                            <li>
                                <a href="/account/logout">Logout</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li>
                                <a href="/account/login">Login</a>
                            </li>
                            <li>
                                <a href="/account/signup">Sign Up</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                    <li><a href="/cart">Cart</a></li>
                </ul>
            </td>
        </tr>
    </table>

</div>