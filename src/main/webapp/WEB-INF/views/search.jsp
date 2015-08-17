<%@ page contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Search</title>
  </head>
  <form method="get" action="search">
    <input type="text" name="q" value="${query}"/>
    <button type="submit">Search</button>
    <c:forEach var="result" items="${results}">
      <br/>
      <a href="${result.url}"><b>${result.title}</b></a><br/>
      <div style="color: darkgray">${result.url}</div>
    </c:forEach>
  </form>
  </body>
</html>
