package it.polimi.tiw.beans;

public class Course {
    private int idcourse;
    private String title;
    private int teacher;

    public Course() {
    }

    public Course(int idcourse, String title, int teacher) {
        this.idcourse = idcourse;
        this.title = title;
        this.teacher = teacher;
    }

    public int getIdcourse() {
        return idcourse;
    }

    public void setIdcourse(int idcourse) {
        this.idcourse = idcourse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTeacher() {
        return teacher;
    }

    public void setTeacher(int teacher) {
        this.teacher = teacher;
    }
}