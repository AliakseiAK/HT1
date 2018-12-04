<%@ page import="app.entities.Person" %>
<%@ page import="app.entities.PhoneNumber" %>
<%@ page import="util.AppConstants" %>
<%@ page import="java.util.HashMap" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<%
    HashMap<String,String> jsp_parameters;
    Person person = null;
    PhoneNumber number = null;

    if (request.getAttribute(AppConstants.PERSON) != null){
        person = (Person)request.getAttribute(AppConstants.PERSON);
    }
    if (request.getAttribute(AppConstants.PHONE_NUMBER) != null){
        number = (PhoneNumber)request.getAttribute(AppConstants.PHONE_NUMBER);
    }
    assert person != null;
    assert number != null;

    String message = person.getFullName();

    if (request.getAttribute("jsp_parameters") != null){
        jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
        if((jsp_parameters.get("error_message") != null)
                && (!jsp_parameters.get("error_message").equals(""))){
            message = jsp_parameters.get("error_message");
        }
    }
%>


<html>
<head>
    <title>Редактирование номера телефона</title>
    <link href="<%=request.getContextPath()%>/css/styles.css" rel="stylesheet">
</head>
<body>
<form action="<%=request.getContextPath()%>/phone/?action=edit" method="post">
    <input type="hidden" name="person_id" value="<%=person.getId()%>"/>
    <input type="hidden" name="phone_id" value="<%=number.getId()%>"/>

    <table border="1" width="70%">
        <tr>
            <td colspan="2">Информация о телефоне владельца: <%=message%></td>
        </tr>
        <tr>
            <td>Номер: </td>
            <td><label>
                <input type="text" name="phoneNumber" value="<%=number.getPhoneNumber()%>"/>
            </label></td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="submit" name="savenumber" value="Сохранить номер" />
                <br>
                <a href="<%=request.getContextPath()%>/person/?action=edit&id=<%=person.getId()%>&id=<%=person.getId()%>">Вернуться к данным о человеке</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
