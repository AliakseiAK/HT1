import app.entities.Person;
import org.testng.Assert;
import org.testng.annotations.Test;
import util.AppUtils;

import static org.testng.Assert.*;

/**
 * A tiny little bit of TDD. Это же учебный проект, не правда ли?)
 */

public class PhonebookAppTest {

    private AppUtils appUtils = new AppUtils();
    private Person personWithMiddlename = new Person("Имя", "Фамилия", "Отчество");
    private Person personNoMiddlename = new Person ("Name", "Surname", "");
    private Person personNullMiddlename = new Person ("Name", "Surname", null);

    @Test
    public void testGetFullName() {
        Assert.assertEquals("Имя Отчество Фамилия", personWithMiddlename.getFullName());
    }

    @Test
    public void testGetPartialName(){
        Assert.assertEquals("Name  Surname", personNoMiddlename.getFullName());
    }

    @Test
    public void testGetPartialNameWithNull(){
        Assert.assertEquals("Name  Surname", personNullMiddlename.getFullName());
    }

    @Test
    public void testValidateFMLNamePartRus() {
        assertTrue(appUtils.validateFMLNamePart(personWithMiddlename.getName(), false));
        assertTrue(appUtils.validateFMLNamePart(personWithMiddlename.getMiddlename(), false));
        assertTrue(appUtils.validateFMLNamePart(personWithMiddlename.getSurname(), false));
    }

    @Test
    public void testValidateFMLNamePartEng() {
        assertTrue(appUtils.validateFMLNamePart(personNoMiddlename.getName(), true));
        assertTrue(appUtils.validateFMLNamePart(personNoMiddlename.getMiddlename(), true));
        assertTrue(appUtils.validateFMLNamePart(personNoMiddlename.getSurname(), true));
    }

    @Test
    public void testValidatePhoneNumberPositive() {
        String correctNumber = "+375-29#1112233";
        Assert.assertEquals(appUtils.validatePhoneNumber(correctNumber), "");
    }

    @Test
    public void testValidatePhoneNumberNegativeWrongCharacter() {
        String correctNumber = "+375-29#111?22.33";
        Assert.assertNotEquals(appUtils.validatePhoneNumber(correctNumber), "");
    }

    @Test
    public void testValidatePhoneNumberNegative() {
        String incorrectNumberTooShort = "3";
        String incorrectNumberTooLong = "111111111111111111111111111111111111111111111111111111111111";//60 символов
        Assert.assertNotEquals(appUtils.validatePhoneNumber(incorrectNumberTooShort), "");
        Assert.assertNotEquals(appUtils.validatePhoneNumber(incorrectNumberTooLong), "");
    }
}