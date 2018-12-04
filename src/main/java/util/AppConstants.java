package util;

/**
 * Константы для идентификаторов значений, используемых в нескольких классах и при
 * передаче данных между классами. Исключают опечатки.
 */
public class AppConstants {
    //
    public static final String DEFAULT_ENCODING = "UTF-8"; //кодировка по умолчанию.
    public static final String REGION = "BY"; //двухбуквенный код по ISO 3166-1

    //Поля Person
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String MIDDLENAME = "middlename";

    //Transitions
    public static final String PERSON = "person";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PHONEBOOK = "phonebook";

    public static final String ACTION = "action";
    public static final String ID = "id";
    public static final String PHONE_ID = "phone_id";
    public static final String PERSON_ID = "person_id";

    //Поля базы данных
    public static final String OWNER = "owner";
    public static final String NUMBER = "number";
    public static final String NEW_NUMBER = "new_number";

    //Поля параметров для JSP.
    public static final String ADD_GO = "add_go";
    public static final String JSP_PARAMETERS = "jsp_parameters";
    public static final String ADD = "add";
    public static final String CURRENT_ACTION = "current_action";
    public static final String NEXT_ACTION = "next_action";
    public static final String NEXT_ACTION_LABEL = "next_action_label";
    public static final String EDIT = "edit";
    public static final String EDIT_GO = "edit_go";
    public static final String CURRENT_ACTION_RESULT = "current_action_result";
    public static final String CURRENT_ACTION_RESULT_LABEL = "current_action_result_label";
    public static final String ERROR_MESSAGE = "error_message";
}
