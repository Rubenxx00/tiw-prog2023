package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Session;

public class SessionDAO {
    private final Connection connection;

    public SessionDAO(Connection connection) {
        this.connection = connection;
    }

    // get all sessions for a course
    public List<Session> getSessionsByCourseId(int courseId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session WHERE course_idcourse = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Session session = new Session(
                            resultSet.getInt("idsession"),
                            resultSet.getInt("course_idcourse"),
                            resultSet.getDate("date"),
                            resultSet.getInt("report_idreport")
                    );
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

    public boolean enrollStudent(int sessionId, int studentId) throws SQLException {
        String query = "INSERT INTO result (student_student_number, session_idsession, grade, state) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, sessionId);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            // check for duplicate entry
            if (e.getErrorCode() == 1062) {
                return false;
            }
            else {
                throw new SQLException(e);
            }
        }
        return true;
    }

    // find by join
    public List<Session> findEnrolledSessionsByStudentId(int login, int selectedCourseId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session " +
                "INNER JOIN result ON session.idsession = result.session_idsession " +
                "WHERE result.student_student_number = ? AND session.course_idcourse = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, login);
            preparedStatement.setInt(2, selectedCourseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Session session = new Session(
                            resultSet.getInt("idsession"),
                            resultSet.getInt("course_idcourse"),
                            resultSet.getDate("date"),
                            resultSet.getInt("report_idreport")
                    );
                    sessions.add(session);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public Session getSessionById(int sessionId) {
        Session session = null;
        String query = "SELECT * FROM session WHERE idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    session = new Session(
                            resultSet.getInt("idsession"),
                            resultSet.getInt("course_idcourse"),
                            resultSet.getDate("date"),
                            resultSet.getInt("report_idreport")
                    );
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return session;
    }

    // check if report_idreport is not null
    public boolean isReported(int sessionId) {
        String query = "SELECT report_idreport FROM session WHERE idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // null value is 0 in java connector
                    return resultSet.getInt("report_idreport") != 0;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
