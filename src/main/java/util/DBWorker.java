package util;

import app.entities.Person;
import app.entities.PhoneNumber;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static util.AppConstants.*;

/**
 * Класс, содержащий методы для операций над базой данных. Реализован паттерн Singleton (eager initialization).
 */

public class DBWorker {
    private Context context;
    private DataSource dataSource;

    // Количество рядов таблицы, затронутых последним запросом.
    private Integer affected_rows = 0;

    // Значение автоинкрементируемого первичного ключа, полученное после добавления новой записи.
    private Integer last_insert_id = 0;

    private static DBWorker instance = null;
    public static DBWorker getInstance() throws NamingException, SQLException {
        if (instance == null){
            instance = new DBWorker();
        }
        return instance;
    }


    private DBWorker() throws NamingException, SQLException {
        context = new InitialContext();
        dataSource = (DataSource) context.lookup("java:comp/env/jdbc/phonebook");
    }

    public Integer getAffectedRowsCount(){
        return this.affected_rows;
    }

    public Integer getLastInsertId(){
        return this.last_insert_id;
    }

    /**
     * Выполнение запросов на выборку данных.
     * @param query строка SQL-запроса.
     * @return
     */
    public ResultSet getDBData(String query) {
        try {
            Connection connect = dataSource.getConnection();
            Statement statement = connect.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("null on getDBData()!");
        return null;
    }


    /**
     * Выполнение запросов на модификацию данных.
     * @param query строка SQL-запроса.
     * @return
     * @throws NamingException
     */
    // TODO: 03.12.2018 return boolean
    public Integer changeDBData(String query) throws NamingException {
        Context context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/phonebook");
        try (Connection connect = dataSource.getConnection()) {
            Statement statement = connect.createStatement();
            this.affected_rows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                this.last_insert_id = rs.getInt(1);
            }

            return this.affected_rows;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("null on changeDBData()!");
        return null;
    }


    /**
     * Возвращает содержимое телефонной книги.
     * @return содержимое телефонной книги.
     * @throws NamingException
     * @throws SQLException
     */
    public HashMap<String, Person> getPhonebook() throws NamingException, SQLException {
        HashMap<String, Person> persons = new HashMap<>();
        ResultSet db_data = getDBData("SELECT * FROM `person` ORDER BY `surname` ASC");
        while (db_data.next()) {
            persons.put(db_data.getString(ID), new Person(db_data.getString(ID),
                    db_data.getString(NAME),
                    db_data.getString(SURNAME),
                    db_data.getString(MIDDLENAME)));
        }
        return persons;
    }

    /**
     * Получение списка телефонов.
     * @param personId идентификатор персоны
     * @return все телефоны персоны.
     * @throws SQLException
     */
    public HashMap<String, PhoneNumber> getPhones(String personId) throws SQLException {
        String query = "SELECT * FROM `phone` WHERE `owner`=" + personId;
        ResultSet db_data = getDBData(query);
        HashMap<String, PhoneNumber> result = new HashMap<>();
        // Если у человека нет телефонов, ResultSet будет == null.
        if (db_data != null){
            while (db_data.next()){
                PhoneNumber phoneNumber = new PhoneNumber(db_data.getInt(AppConstants.ID),
                        db_data.getInt(AppConstants.OWNER),
                        db_data.getString(AppConstants.NUMBER));
                result.put(db_data.getString(AppConstants.ID), phoneNumber);
            }
        }
        return result;
    }
}

