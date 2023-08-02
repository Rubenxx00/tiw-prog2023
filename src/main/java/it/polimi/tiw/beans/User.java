package it.polimi.tiw.beans;

public class User {
    private int login;
    private String name;
    private String surname;
    private String password;
    private String role;
    private String email;

    public User() {
    }

    public User(int login, String name, String surname, String password, String role, String email) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
    	this.email = email;
    }

    public String getEmail() {
    	return email;
    }
}
