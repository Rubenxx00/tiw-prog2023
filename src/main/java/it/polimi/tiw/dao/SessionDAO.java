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
                    Session session = new Session();
                    session.setIdsession(resultSet.getInt("idsession"));
                    session.setDate(resultSet.getDate("date"));
                    session.setCourse_idcourse(resultSet.getInt("course_idcourse"));
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

}
