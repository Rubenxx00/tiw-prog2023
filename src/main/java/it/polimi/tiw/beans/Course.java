package it.polimi.tiw.beans;

public class Course {
    private int idcourse;
    private String title;
    private int teacher_idteacher;
    private Teacher teacher;

    public Course() {
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

    public int getTeacher_idteacher() {
        return teacher_idteacher;
    }

    public void setTeacher_idteacher(int teacher_idteacher) {
        this.teacher_idteacher = teacher_idteacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    // override equals and hashcode
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Course))
            return false;
        Course course = (Course) obj;
        return course.getIdcourse() == this.idcourse;
    }

    @Override
    public int hashCode() {
        return idcourse;
    }
}