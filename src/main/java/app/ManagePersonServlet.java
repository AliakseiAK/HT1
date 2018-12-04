package app;

import app.entities.Person;
import app.entities.Phonebook;
import util.AppConstants;
import util.AppUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static util.AppConstants.*;

/**
 * Сервлет, подготавливающий данные для ManagePerson.jsp
 */
public class ManagePersonServlet extends HttpServlet {
    // Идентификатор для сериализации/десериализации.
    private static final long serialVersionUID = 1L;
    // Основной объект, хранящий данные телефонной книги.
    private Phonebook phonebook;
    private AppUtils appUtils = new AppUtils();

    public ManagePersonServlet() throws NamingException {
        super();
        try{
            this.phonebook = Phonebook.getInstance();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(AppConstants.DEFAULT_ENCODING);
        // Хранилище параметров для передачи в JSP.
        HashMap<String,String> jsp_parameters = new HashMap<>();

        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/ManageList.jsp");

        // TODO: 27.11.2018 NPE?
        // Действие (action) и идентификатор записи (id) над которой выполняется это действие.
        String action = request.getParameter(AppConstants.ACTION);
        String personId = request.getParameter(ID);

        // Если идентификатор и действие не указаны, мы находимся в состоянии
        // "просто показать список и больше ничего не делать".
        if ((action == null)&&(personId == null))
        {
            request.setAttribute(JSP_PARAMETERS, jsp_parameters);
            dispatcher_for_list.forward(request, response);
        }
        // Если же действие указано, то...
        else{
            assert action != null;
            switch (action) {
                // Добавление записи.
                case "add":
                    // Создание новой пустой записи о пользователе.
                    Person empty_person = new Person();

                    // Подготовка параметров для JSP.
                    jsp_parameters.put(CURRENT_ACTION, AppConstants.ADD);
                    jsp_parameters.put(NEXT_ACTION, AppConstants.ADD_GO);
                    jsp_parameters.put(NEXT_ACTION_LABEL, "Добавить");

                    // Установка параметров JSP.
                    request.setAttribute(PERSON, empty_person);
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;

                // Редактирование записи.
                case "edit":
                    // Извлечение из телефонной книги информации о редактируемой записи.

                    Person editable_person = phonebook.getPerson(personId);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put(CURRENT_ACTION, EDIT);
                    jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                    jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute(PERSON, editable_person);
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;

                // Удаление записи.
                case "delete":
                    // Если запись удалось удалить...
                    try {
                        if (phonebook.deletePerson(personId)) {
                            jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_SUCCESS");
                            jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Удаление выполнено успешно");
                            request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                            dispatcher_for_list.forward(request, response);
                        }
                        // Если запись не удалось удалить (например, такой записи нет)...
                        else {
                            jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_FAILURE");
                            jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка удаления (возможно, запись не найдена)");
                            request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                            dispatcher_for_list.forward(request, response);
                        }
                    } catch (NamingException | SQLException e) {
                        e.printStackTrace();
                    }


                    break;

                case "deletePhone":
                    String phoneId = request.getParameter(AppConstants.PHONE_ID);
                    String ownerId = request.getParameter(AppConstants.PERSON_ID);

                    jsp_parameters.put(CURRENT_ACTION, EDIT);
                    jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                    jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");


                    try {
                        if (phonebook.deletePhone(ownerId, phoneId)) {
                            jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_SUCCESS");
                            jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Удаление выполнено успешно");
                        } else {
                            jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_FAILURE");
                        }
                    } catch (NamingException | SQLException e) {
                        e.printStackTrace();
                    }

                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                    dispatcher_for_manager.forward(request, response);
                    break;


            }
        }

    }

    // Реакция на POST-запросы.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding(AppConstants.DEFAULT_ENCODING);
        request.setAttribute(AppConstants.PHONEBOOK, this.phonebook);
        HashMap<String,String> jsp_parameters = new HashMap<>();

        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/ManageList.jsp");

        // Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
        String add_go = request.getParameter(AppConstants.ADD_GO);
        String edit_go = request.getParameter(EDIT_GO);

        // Просмотр записи.
        if (add_go == null){
            // Подготовка параметров для JSP.
            jsp_parameters.put(CURRENT_ACTION, EDIT);
            jsp_parameters.put(NEXT_ACTION, EDIT_GO);
            jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
            jsp_parameters.put(ERROR_MESSAGE, "");

            String personId = request.getParameter(ID);
            request.setAttribute(PERSON, phonebook.getPerson(personId));
            request.setAttribute(JSP_PARAMETERS, jsp_parameters);
            dispatcher_for_manager.forward(request, response);
        } else {
            // Создание записи на основе данных из формы.
            Person new_person = new Person(
                    request.getParameter(NAME).trim(),
                    request.getParameter(SURNAME).trim(),
                    request.getParameter(MIDDLENAME).trim());

            // Валидация ФИО.
            String error_message = appUtils.validatePersonFMLName(new_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals(""))
            {

                // Если запись удалось добавить...
                try {
                    if (phonebook.addPerson(new_person))
                    {
                        jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");
                    }
                    // Если запись НЕ удалось добавить...
                    else
                    {
                        jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_FAILURE");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка добавления");
                    }
                } catch (NamingException | SQLException e) {
                    e.printStackTrace();
                }

                // Установка параметров JSP.
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            } else {
                // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.

                // Подготовка параметров для JSP.
                jsp_parameters.put(CURRENT_ACTION, ADD);
                jsp_parameters.put(NEXT_ACTION, AppConstants.ADD_GO);
                jsp_parameters.put(NEXT_ACTION_LABEL, "Добавить");
                jsp_parameters.put(ERROR_MESSAGE, error_message);

                // Установка параметров JSP.
                request.setAttribute(PERSON, new_person);
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
        }

        // Редактирование записи.
        if (edit_go != null)
        {
            // Получение записи и её обновление на основе данных из формы.
            Person updatable_person = this.phonebook.getPerson(request.getParameter(ID));
            updatable_person.setName(request.getParameter(NAME));
            updatable_person.setSurname(request.getParameter(SURNAME));
            updatable_person.setMiddlename(request.getParameter(MIDDLENAME));

            // Валидация ФИО.
            String error_message = appUtils.validatePersonFMLName(updatable_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals(""))
            {
                // Если запись удалось обновить...
                try {
                    if (this.phonebook.updatePerson(updatable_person))
                    {
                        jsp_parameters.put(CURRENT_ACTION_RESULT, "UPDATE_SUCCESS");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Обновление выполнено успешно");
                    }
                    // Если запись НЕ удалось обновить...
                    else
                    {
                        jsp_parameters.put(CURRENT_ACTION_RESULT, "UPDATE_FAILURE");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка обновления");
                    }
                } catch (NamingException | SQLException e) {
                    e.printStackTrace();
                }

                // Установка параметров JSP.
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }else{
                // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.

                // Подготовка параметров для JSP.
                jsp_parameters.put(CURRENT_ACTION, EDIT);
                jsp_parameters.put(NEXT_ACTION, EDIT_GO);
                jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
                jsp_parameters.put(ERROR_MESSAGE, error_message);

                // Установка параметров JSP.
                request.setAttribute(PERSON, updatable_person);
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
        }
    }
}
