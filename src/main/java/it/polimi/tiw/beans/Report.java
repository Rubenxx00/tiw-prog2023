package it.polimi.tiw.beans;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Report {
    private int idreport;
    private Timestamp date;
    private List<Result> rows;
    private Session session;

    public Report() {
    }

    public Report(int idreport, int session_idsession, Timestamp date) {
        this.idreport = idreport;
        this.date = date;
    }

    public int getIdreport() {
        return idreport;
    }

    public void setIdreport(int idreport) {
        this.idreport = idreport;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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
