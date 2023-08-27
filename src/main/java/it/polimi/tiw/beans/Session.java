package it.polimi.tiw.beans;

import java.sql.Timestamp;
import java.util.Date;

public class Session {
    private int idsession;
    private int course_idcourse;
    private Timestamp date;
    private Integer report_idreport;

    public Session() {}

    public Session(int idsession, int course_idcourse, Timestamp date, Integer report_idreport) {
        this.idsession = idsession;
        this.course_idcourse = course_idcourse;
        this.date = date;
        this.report_idreport = report_idreport == 0 ? null : report_idreport;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getReport_idreport() { return report_idreport; }

    public void setReport_idreport(Integer report_idreport) { this.report_idreport = report_idreport == 0 ? null : report_idreport; }

    public boolean isReported() {
        return report_idreport != null;
    }
}
