package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Report;
import it.polimi.tiw.beans.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {
    private final Connection connection;

    public ReportDAO(Connection connection) {
        this.connection = connection;
    }

    // get report by session id
    public Report getReportBySessionId(int sessionId) throws SQLException {
        Report report = null;
        String query = "SELECT * FROM report WHERE session_idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    report = new Report(
                            resultSet.getInt("idreport"),
                            resultSet.getInt("session_idsession"),
                            resultSet.getDate("datetime")
                    );
                    report.setSession(new SessionDAO(connection).getSessionById(sessionId));
                }
            }
        }
        // load report rows (results)
        if (report != null) {
            report.setRows(new ResultDAO(connection).getResultsBySessionId(report.getSession_idsession()));
        }
        return report;
    }

    public boolean isResultRecorded(int sessionId) {
        String query = "SELECT COUNT(*) FROM report WHERE session_idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Report getSessionByReportId(int reportId) throws SQLException {
        Report report = null;
        String query = "SELECT * FROM report WHERE idreport = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reportId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    report = new Report(
                            resultSet.getInt("id"),
                            resultSet.getInt("session_idsession"),
                            resultSet.getDate("datetime")
                    );
                    report.setSession(new SessionDAO(connection).getSessionById(report.getSession_idsession()));
                }
            }
        }
        // load report rows (results)
        if (report != null) {
            report.setRows(new ResultDAO(connection).getResultsBySessionId(report.getSession_idsession()));
        }
        return report;
    }
}
