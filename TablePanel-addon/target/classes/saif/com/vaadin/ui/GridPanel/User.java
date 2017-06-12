

package saif.com.vaadin.ui.GridPanel;

import java.util.Date;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class User {
    public static enum Type{
        MAN,WOMEN,UNIVERSAL;
    }
    private String firstName;
    private String lastName;
    private int age;
    private Date birthDate;
    private boolean man;
    private Type type;

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMan() {
        return man;
    }

    public void setMan(boolean man) {
        this.man = man;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
