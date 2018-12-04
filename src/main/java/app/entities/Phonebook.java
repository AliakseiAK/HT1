package app.entities;

import java.sql.SQLException;
import java.util.HashMap;

import util.DBWorker;
import util.AppUtils;

import javax.naming.NamingException;

/**
 * Класс, описывающий телефонную книжку.
 * Обеспечивает основные операции над записями: извлечение, добавление, удаление, редактирование.
 * Реализован Singleton (eager initialization).
 */
public class Phonebook {
    // Хранилище записей о людях.
    private HashMap<String, Person> persons = new HashMap<>();

    // Объект для работы с БД.
    private DBWorker dbWorker = DBWorker.getInstance();

    private static Phonebook instance = null;
    public static Phonebook getInstance() throws SQLException, NamingException {
        if (instance == null){
            instance = new Phonebook();
        }
        return instance;
    }

    private AppUtils appUtils = new AppUtils();


    /**
     * При создании экземпляра класса из БД извлекаются все записи.
     */
    private Phonebook() throws SQLException, NamingException {
        persons = this.dbWorker.getPhonebook();
    }

    /**
     * Получение записи о персоне из базы.
     * @param id идентификатор персоны.
     * @return объект персоны.
     */
    public Person getPerson(String id)
    {
        return this.persons.get(id);
    }

    /**
     * Добавление записи о человеке из базы.
     * @param person Объект для записи в базу данных.
     * @return true, если запись прошла успешно.
     * @throws NamingException
     */
    public boolean addPerson(Person person) throws NamingException, SQLException {
        String query;
        // У человека может не быть отчества.
        if (!person.getMiddlename().equals("")){
            query = "INSERT INTO `person` (`name`, `surname`, `middlename`) VALUES ('"
                    + person.getName() +"', '"
                    + person.getSurname() +"', '"
                    + person.getMiddlename() + "')";
        }else{
            query = "INSERT INTO `person` (`name`, `surname`) VALUES ('"
                    + person.getName() +"', '"
                    + person.getSurname() +"')";
        }

        Integer affected_rows = this.dbWorker.changeDBData(query);

        if (affected_rows > 0){
            person.setId(this.dbWorker.getLastInsertId().toString());
            this.persons.put(person.getId(), person);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Обновление записи о человеке в базе. Предусмотрены варианты, когда у человека есть либо нет отчества.
     * @param person Объект для записи в базу данных.
     * @return true, если операция прошла успешно.
     * @throws NamingException
     */
    public boolean updatePerson(Person person) throws NamingException, SQLException {
        int id_filtered = Integer.parseInt(person.getId());
        String query = "";
        if (!person.getMiddlename().equals("")){
            query = "UPDATE `person` SET `name` = '" + person.getName() + "'," +
                    " `surname` = '" + person.getSurname() + "', " +
                    "`middlename` = '" + person.getMiddlename() +
                    "' WHERE `id` = " + id_filtered;
        }else{
            query = "UPDATE `person` SET `name` = '" + person.getName() + "', " +
                    "`surname` = '" + person.getSurname() +
                    "' WHERE `id` = " + id_filtered;
        }

        Integer affected_rows = this.dbWorker.changeDBData(query);

        if (affected_rows > 0){
            this.persons.put(person.getId(), person);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Удаление записи о персоне из базы
     * @param id идентификатор персоны.
     * @return true, если операция прошла успешно.
     * @throws NamingException
     * @throws SQLException
     */
    public boolean deletePerson(String id) throws NamingException, SQLException {
        if ((id != null)&&(!id.equals("null"))){
            int filtered_id = Integer.parseInt(id);
            Integer affected_rows = this.dbWorker.changeDBData("DELETE FROM `person` WHERE `id`=" + filtered_id);

            if (affected_rows > 0){
                this.persons.remove(id);
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * Удаление из БД записи о телефоне персоны.
     * @param ownerId идентификатор персоны.
     * @param phoneId идентификатор телефона.
     * @return true, если операция прошла успешно.
     * @throws SQLException
     * @throws NamingException
     */
    public boolean deletePhone(String ownerId, String phoneId) throws SQLException, NamingException {
        if ((phoneId != null)&&(!phoneId.equals("null"))){
            int filtered_id = Integer.parseInt(phoneId); // TODO: 02.12.2018 do we need parsing?
            Integer affected_rows = this.dbWorker.changeDBData("DELETE FROM `phone` WHERE `id`=" + filtered_id);
            if (affected_rows > 0){
                this.persons.get(ownerId).getPhones().remove(phoneId);
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * Запись номера телефона в базу.
     * @param phoneNumber объект записи о телефоне.
     * @return true, если операция прошла успешно.
     * @throws NamingException
     */
    public String addPhone(PhoneNumber phoneNumber) throws NamingException {
        int ownerId = phoneNumber.getOwnerId();
        String number = phoneNumber.getPhoneNumber();
            String query = "INSERT INTO `phone` (`owner`, `number`) VALUES ('"
                    + ownerId +"', '"
                    + number + "')";

            Integer affected_rows = this.dbWorker.changeDBData(query);

            if (affected_rows > 0){
                phoneNumber.setId(this.dbWorker.getLastInsertId());
                this.persons.get(String.valueOf(ownerId)).getPhones()
                        .put(String.valueOf(phoneNumber.getId()), phoneNumber);
                return "";
            }else{
                return "Error on database write";
            }
    }

    /**
     * Обновление номера телефона в базе.
     * @param phoneNumber объект записи о телефоне.
     * @return true, если операция прошла успешно.
     * @throws NamingException
     */
    public boolean updatePhone(PhoneNumber phoneNumber) throws NamingException {
        int ownerId = phoneNumber.getOwnerId();
        int id_filtered = phoneNumber.getId();
        String query = "UPDATE `phone` SET `number` = '" + phoneNumber.getPhoneNumber() + "' "
                + "WHERE `id` = " + id_filtered;
        Integer affected_rows = this.dbWorker.changeDBData(query);

        if (affected_rows > 0){
            this.persons.get(String.valueOf(ownerId)).getPhones()
                    .put(String.valueOf(phoneNumber.getId()), phoneNumber);
            return true;
        }else{
            return false;
        }
    }


    public HashMap<String,Person> getPersons()
    {
        return persons;
    }

    public void setPersons(HashMap<String, Person> persons) {
        this.persons = persons;
    }
}
