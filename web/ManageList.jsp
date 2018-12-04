<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.entities.Person"%>
<%@ page import="app.entities.PhoneNumber"%>
<%@ page import="app.entities.Phonebook"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="javax.naming.NamingException" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Список людей</title>
    <link href="<%=request.getContextPath()%>/css/styles.css" rel="stylesheet">
</head>
<body>

<%
    String user_message;
    Phonebook phonebook = null;
    HashMap<String,String> jsp_parameters = new HashMap<>();

    try {
        phonebook = Phonebook.getInstance();
    } catch (SQLException | NamingException e) {
        e.printStackTrace();
    }

    if (request.getAttribute("jsp_parameters") != null){
        jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
    }

    user_message = jsp_parameters.get("current_action_result_label");
%>

<table width="90%">

    <%if ((user_message != null)&&(!user_message.equals(""))){%>
    <tr>
        <td colspan="6"><%=user_message%></td>
    </tr>
    <%}%>

    <tr>
        <th colspan="6"><a href="<%=request.getContextPath()%>/person/?action=add">Добавить запись</a></th>
    </tr>
    <tr>
        <td><b>Фамилия</b></td>
        <td><b>Имя</b></td>
        <td><b>Отчество</b></td>
        <td><b>Телефон(ы)</b></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>

    <%
        assert phonebook != null;
        for (Map.Entry<String, Person> entry : phonebook.getPersons().entrySet()) {
        Person person = entry.getValue();%>
    <tr>
        <td><%=person.getSurname()%></td>
        <td><%=person.getName()%></td>
        <td><%=person.getMiddlename()%></td>
        <td align="left">
            <%for(PhoneNumber phone : person.getPhones().values()){%>
            <%=phone.getPhoneNumber()%><br />
            <%}%>
        </td>
        <td><a href="<%=request.getContextPath()%>/person/?action=edit&id=<%=person.getId()%>">Редактировать</a></td>
        <td><a href="<%=request.getContextPath()%>/person/?action=delete&id=<%=person.getId()%>">Удалить</a></td>
    </tr>
    <%}%>

</table>

</body>
</html>