package it.polimi.tiw.beans;

public class Teacher extends User{
    public Teacher(int login, String name, String surname, String email) {
        super(login, name, surname, "", "teacher", email);
    }
}
