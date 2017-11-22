<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set scope="request" var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<body>
<h2>Hello World!</h2>
<h3><a href="${ctx}/process/test">HELLO</a></h3>
</body>
</html>
