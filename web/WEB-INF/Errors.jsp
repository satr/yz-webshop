<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="error-message">
    <c:forEach items="${errors}" var="error">
        <c:out value="${error}" /><br />
    </c:forEach>
</div>

