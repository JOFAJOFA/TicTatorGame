<%-- 
    Document   : showGames
    Created on : Jun 8, 2016, 2:09:02 PM
    Author     : Ferenc_S
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Active Games</title>
    </head>
    <body>
        <table border="1" style="width:100%">
            <tr>
                <th align="left" width="10%">Nth</th>
                <th align="left" width="30%">Id</th>
                <th align="left" width="20%">Date</th>
                <th align="left" width="20%">Username 1</th>
                <th align="left" width="20%">Username 2</th>
            </tr>
            <c:forEach var="game" items="${games}" varStatus="status">
                <tr>
                    <td><c:out value="${status.count}"/></td>
                    <td><c:out value="${game.id}"/> </td>
                    <td><c:out value="${game.createdAt}"/> </td>
                    <td><c:out value="${game.username_1}"/> </td>
                    <td><c:out value="${game.username_2}"/> </td>
                </tr>
            </c:forEach>            
        </table>
    </body>
</html>
