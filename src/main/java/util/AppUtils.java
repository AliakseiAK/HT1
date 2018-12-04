package util;

import app.entities.Person;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для общих методов (валидация).
 */
public class AppUtils {

    public AppUtils() {
    }

    // Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных.
    public String validatePersonFMLName(Person person){
        String error_message = "";

        if (validateFMLNamePart(person.getName(), false))
        {
            error_message += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        if (validateFMLNamePart(person.getSurname(), false))
        {
            error_message += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        if (validateFMLNamePart(person.getMiddlename(), true))
        {
            error_message += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        return error_message;
    }

    // Валидация частей ФИО. Для отчества можно передать второй параетр == true,
    // тогда допускается пустое значение.
    public boolean validateFMLNamePart(String fml_name_part, boolean empty_allowed){
        if (empty_allowed){
            Matcher matcher = Pattern.compile("[\\w-]{0,150}", Pattern.UNICODE_CHARACTER_CLASS).matcher(fml_name_part);
            return matcher.matches();
        }
        else{
            Matcher matcher = Pattern.compile("[\\w-]{1,150}", Pattern.UNICODE_CHARACTER_CLASS).matcher(fml_name_part);
            return matcher.matches();
        }

    }

    public String validatePhoneNumber(String number) {
        Matcher matcher = Pattern.compile("[\\d-+#]{2,50}").matcher(number);
        if (matcher.matches()){
            return "";
        } else {
            return "Проверьте указанный номер. Длина от 2 до 50 символов. Допускаются цифры, знаки +, -, #.<br>";
        }
    }
}
