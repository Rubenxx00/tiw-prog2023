package it.polimi.tiw.beans;

import java.util.Date;
import java.util.List;

public class Report {
    private int idreport;
    private int session_idsession;
    private Date date;
    private List<Result> rows;
    private Session session;

    public Report() {
    }

    public Report(int idreport, int session_idsession, Date date) {
        this.idreport = idreport;
        this.session_idsession = session_idsession;
        this.date = date;
    }

    public int getIdreport() {
        return idreport;
    }

    public void setIdreport(int idreport) {
        this.idreport = idreport;
    }

    public int getSession_idsession() {
        return session_idsession;
    }

    public void setSession_idsession(int session_idsession) {
        this.session_idsession = session_idsession;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Result> getRows() {
        return rows;
    }

    public void setRows(List<Result> rows) {
        this.rows = rows;
    }
}
