package it.polimi.tiw.beans;

public class Result {
    private int student_student_number;
    private int session_idsession;
    private Integer grade;
    private ResultState state;
    private Student student;

    // Constructors, getters, setters, and other methods

    // Default constructor
    public Result() {}

    // Constructor with all fields
    public Result(int student_student_number, int session_idsession, Integer grade, int state) {
        this.student_student_number = student_student_number;
        this.session_idsession = session_idsession;
        this.grade = grade;
        this.state = ResultState.fromValue(state);
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

    public ResultState getState() {
        return state;
    }

    public void setState(ResultState state) {
        this.state = state;
    }

    public Student getStudent() { return student; }

    public void setStudent(Student student) { this.student = student; }

    // Other methods
    public boolean isEditableByTeacher() {
        return this.state.getValue() <= ResultState.INSERITO.getValue();
    }

    public boolean isEditableByStudent() {
        return this.state.getValue() == ResultState.PUBBLICATO.getValue();
    }

    public String getGradeString() {
        if (grade == 0) {
            return "N/A";
        } else if (grade == 31) {
            return "30 e Lode";
        } else {
            return grade.toString();
        }
    }

    public boolean isRejected() {
        return this.state.getValue() == ResultState.RIFIUTATO.getValue();
    }
}

