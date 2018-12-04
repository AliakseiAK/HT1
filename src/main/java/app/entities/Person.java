package app.entities;

import util.DBWorker;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Класс, описывающий персону. Реализована валидация ФИО.
 */
public class Person {

    // Данные записи о человеке.
    private String id;
    private String name;
    private String surname;
    private String middlename;
    private HashMap<String, PhoneNumber> phones = new HashMap<>();

    // Конструктор для создания записи о человеке на основе данных из БД.
    public Person(String personId, String name, String surname, String middlename) throws NamingException, SQLException {
        this.id = personId;
        this.name = name;
        this.surname = surname;
        this.middlename = middlename;
        this.phones.putAll(DBWorker.getInstance().getPhones(personId));
    }

    public Person(){
        this.id = "0";
        this.name = "";
        this.surname = "";
        this.middlename = "";
    }

    // Конструктор для создания записи, предназначенной для добавления в БД.
    public Person(String name, String surname, String middlename){
        this.id = "0";
        this.name = name;
        this.surname = surname;
        this.middlename = middlename;
    }


    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getSurname()
    {
        return this.surname;
    }

    public String getFullName()
    {
        String middlename = this.middlename != null ? this.middlename : "";
        return this.name + " " + middlename + " " + this.surname;
    }

    public String getMiddlename(){
        if ((this.middlename != null)){
            return this.middlename;
        }
        else{
            return "";
        }
    }

    public HashMap<String,PhoneNumber> getPhones(){
        return this.phones;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public void setMiddlename(String middlename)
    {
        this.middlename = middlename;
    }
}