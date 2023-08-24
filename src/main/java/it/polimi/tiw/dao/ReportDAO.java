package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Report;
import it.polimi.tiw.beans.ResultState;
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

    // set ResultState.VERBALIZZATO for all results in session
    public int createReport(int sessionId) throws SQLException {
        //begin transaction
        connection.setAutoCommit(false);
        try {
            String query = "UPDATE result SET state = ? WHERE session_idsession = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, ResultState.VERBALIZZATO.getValue());
                preparedStatement.setInt(2, sessionId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            // insert new blank row in report table and get id
            query = "INSERT INTO report () VALUES ()";
            int reportId;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        reportId = resultSet.getInt(1);
                    } else {
                        throw new SQLException("Creating report failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            // update report_idreport in session table
            query = "UPDATE session SET report_idreport = ? WHERE idsession = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, reportId);
                preparedStatement.setInt(2, sessionId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            //commit transaction
            connection.commit();
            return reportId;
        } catch (SQLException e) {
            //rollback transaction
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // get report by id
    public Report getReportByReportId(int reportId) throws SQLException {
        Report report = new Report();
        String query = "SELECT * FROM report " +
                "INNER JOIN session ON report.idreport = session.report_idreport " +
                "WHERE report.idreport = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reportId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    report.setIdreport(resultSet.getInt("idreport"));
                    report.setDate(resultSet.getDate("date"));
                    Session session = new Session(
                            resultSet.getInt("idsession"),
                            resultSet.getInt("course_idcourse"),
                            resultSet.getDate("date"),
                            resultSet.getInt("report_idreport")
                    );
                    report.setSession(session);
                    // get rows
                    report.setRows(new ResultDAO(connection).getResultsBySessionId(report.getSession().getIdsession()));
                }
            }
        }
        return report;
    }

}
