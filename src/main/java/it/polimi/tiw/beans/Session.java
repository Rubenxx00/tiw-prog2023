package it.polimi.tiw.beans;

import java.util.Date;

public class Session {
    private int idsession;
    private int course_idcourse;
    private Date date;

    public Session() {}

    public Session(int idsession, int course_idcourse, Date date) {
        this.idsession = idsession;
        this.course_idcourse = course_idcourse;
        this.date = date;
    }

    public int getIdsession() {
        return idsession;
    }

    public void setIdsession(int idsession) {
        this.idsession = idsession;
    }

    public int getCourse_idcourse() {
        return course_idcourse;
    }

    public void setCourse_idcourse(int course_idcourse) {
        this.course_idcourse = course_idcourse;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
