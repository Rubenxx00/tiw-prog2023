package it.polimi.tiw.beans;

public class Result {
    private int student_student_number;
    private int session_idsession;
    private Integer grade;
    private String state;

    // Constructors, getters, setters, and other methods

    // Default constructor
    public Result() {}

    // Constructor with all fields
    public Result(int student_student_number, int session_idsession, Integer grade, String state) {
        this.student_student_number = student_student_number;
        this.session_idsession = session_idsession;
        this.grade = grade;
        this.state = state;
    }

    // Getters and setters for each property
    public int getStudent_student_number() {
        return student_student_number;
    }

    public void setStudent_student_number(int student_student_number) {
        this.student_student_number = student_student_number;
    }

    public int getSession_idsession() {
        return session_idsession;
    }

    public void setSession_idsession(int session_idsession) {
        this.session_idsession = session_idsession;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

