package app;

import app.entities.Person;
import app.entities.PhoneNumber;
import app.entities.Phonebook;
import util.AppUtils;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import static util.AppConstants.*;

/**
 * Сервлет, подготавливающий данные для AddPhone.jsp и EditPhone.jsp.
 */
public class ManagePhoneServlet extends HttpServlet {

    private Phonebook phonebook;
    private AppUtils appUtils;

    public ManagePhoneServlet() throws NamingException {
        super();
        try{
            this.phonebook = Phonebook.getInstance();
        } catch (SQLException e){
            e.printStackTrace();
        }
        appUtils = new AppUtils();
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding(DEFAULT_ENCODING);
        HashMap<String,String> jsp_parameters = new HashMap<>();
        RequestDispatcher dispatcher_for_addPhone = request.getRequestDispatcher("/AddPhone.jsp");
        RequestDispatcher dispatcher_for_editPhone = request.getRequestDispatcher("/EditPhone.jsp");

        String action = request.getParameter(ACTION);
        String phoneId = request.getParameter(PHONE_ID);
        String personId = request.getParameter(PERSON_ID);
        Person person = phonebook.getPerson(personId);


        switch (action){
            case "addPhone":
                jsp_parameters.put(CURRENT_ACTION, ADD);
                jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
                request.setAttribute(PERSON, person);

                request.setAttribute(JSP_PARAMETERS, jsp_parameters);
                dispatcher_for_addPhone.forward(request, response);
                break;

            case "editPhone":
                PhoneNumber editable_number = person.getPhones().get(phoneId);

                jsp_parameters.put(CURRENT_ACTION, EDIT);
                jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");

                request.setAttribute(PERSON, person);
                request.setAttribute(PHONE_NUMBER, editable_number);
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);
                dispatcher_for_editPhone.forward(request, response);
                break;
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(DEFAULT_ENCODING);
        request.setAttribute(PHONEBOOK, this.phonebook);
        HashMap<String,String> jsp_parameters = new HashMap<>();
        RequestDispatcher dispatcher_for_person = request.getRequestDispatcher("/ManagePerson.jsp");
        //Поскольку проект учебный, добавлен альтернативный вариант с двумя диспетчерами,
        // а не одним с переключающимися режимами, как в ManagePersonServlet.
        RequestDispatcher dispatcher_for_add = request.getRequestDispatcher("/AddPhone.jsp");
        RequestDispatcher dispatcher_for_edit = request.getRequestDispatcher("/EditPhone.jsp");

        String action = request.getParameter(ACTION);
        String number = request.getParameter(PHONE_NUMBER);
        String personId = request.getParameter(PERSON_ID);
        String phoneId = request.getParameter(PHONE_ID);
        Person person = phonebook.getPerson(personId);

        String error_message = appUtils.validatePhoneNumber(number);

        switch (action){
            case EDIT:
                if (error_message.equals("")){
                    PhoneNumber editable_number = person.getPhones().get(phoneId);
                    editable_number.setPhoneNumber(number);
                    try {
                        phonebook.updatePhone(editable_number);
                    } catch (NamingException e) {
                        e.printStackTrace();
                    }

                    // Подготовка параметров для JSP.
                    jsp_parameters.put(CURRENT_ACTION, EDIT);
                    jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                    jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
                    jsp_parameters.put(ERROR_MESSAGE, "");
                    jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
                    jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");


                    request.setAttribute(PERSON, person);
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);
                    dispatcher_for_person.forward(request, response);
                } else {
                    // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
                    // Подготовка параметров для JSP.
                    jsp_parameters.put(ERROR_MESSAGE, error_message);
                    request.setAttribute(PERSON, person);
                    // Установка параметров JSP.
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);
                    dispatcher_for_edit.forward(request, response);
                }

                break;

            case ADD:
                if (error_message.equals("")){
                    PhoneNumber phoneNumber = new PhoneNumber();
                    phoneNumber.setPhoneNumber(number);
                    phoneNumber.setOwnerId(Integer.parseInt(personId));
                    try {
                        phonebook.addPhone(phoneNumber);

                        jsp_parameters.put(CURRENT_ACTION, ADD);
                        jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                        jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
                        jsp_parameters.put(ERROR_MESSAGE, "");
                        jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");

                        request.setAttribute(PERSON, phonebook.getPerson(personId));
                        request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                        dispatcher_for_person.forward(request, response);

                    } catch (NamingException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
                    // Подготовка параметров для JSP.
                    jsp_parameters.put(ERROR_MESSAGE, error_message);
                    request.setAttribute(PERSON, person);
                    // Установка параметров JSP.
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);
                    dispatcher_for_add.forward(request, response);
                }
                break;
        }
    }
}

