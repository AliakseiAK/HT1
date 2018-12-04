package app;

import app.entities.Person;
import app.entities.Phonebook;
import util.AppConstants;
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

/**
 * Сервлет, подготавливающий данные для ManageList.jsp. Обработка общего списка персон.
 */
public class ManageListServlet extends HttpServlet {
    // Идентификатор для сериализации/десериализации.
    private static final long serialVersionUID = 1L;

    // Основной объект, хранящий данные телефонной книги.
    private Phonebook phonebook;

    //Класс с полезными утилитами
    private AppUtils appUtils;

    public ManageListServlet() throws NamingException {
        super();
        appUtils = new AppUtils();
        try{
            this.phonebook = Phonebook.getInstance();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(AppConstants.DEFAULT_ENCODING);
        request.setAttribute(AppConstants.PHONEBOOK, this.phonebook);
        try {
            processRequest(request, response);
        } catch (SQLException | ClassNotFoundException | NamingException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException, ClassNotFoundException, NamingException {
        request.setCharacterEncoding(AppConstants.DEFAULT_ENCODING);
        Phonebook phonebook = Phonebook.getInstance();
        request.setAttribute(AppConstants.PHONEBOOK, phonebook);
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/ManageList.jsp");
        dispatcher_for_list.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(AppConstants.DEFAULT_ENCODING);
        request.setAttribute(AppConstants.PHONEBOOK, this.phonebook);
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();

        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/ManageList.jsp");

        // Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
        String add_go = request.getParameter(AppConstants.ADD_GO);
        String error_message;

        // Добавление записи.
        if (add_go != null) {
            // Создание записи на основе данных из формы.
            Person new_person = new Person(request.getParameter(AppConstants.NAME),
                    request.getParameter(AppConstants.SURNAME),
                    request.getParameter(AppConstants.MIDDLENAME));

            // Валидация ФИО.
            error_message = appUtils.validatePersonFMLName(new_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {
                // Если запись удалось добавить...
                try {
                    if (this.phonebook.addPerson(new_person)) {
                        jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                    }
                    // Если запись НЕ удалось добавить...
                    else {
                        jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                    }
                } catch (NamingException | SQLException e) {
                    e.printStackTrace();
                }

                // Установка параметров JSP.
                request.setAttribute(AppConstants.JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
        }
    }
}

