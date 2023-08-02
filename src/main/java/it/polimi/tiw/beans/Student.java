package it.polimi.tiw.beans;

public class Student extends User {
    private int student_number;
    private String school;

    // Constructors, getters, setters, and other methods

    // Default constructor
    public Student() { super(); }

    // Constructor with all fields
    public Student(int student_number, String school) {
        super();
        this.student_number = student_number;
        this.school = school;
    }

    // Getters and setters for each property
    public int getStudent_number() {
        return student_number;
    }

    public void setStudent_number(int student_number) {
        this.student_number = student_number;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
