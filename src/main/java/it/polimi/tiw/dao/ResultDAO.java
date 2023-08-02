package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Result;
import it.polimi.tiw.beans.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {
    private final Connection connection;
    public ResultDAO(Connection connection) {
        this.connection = connection;
    }

    // get all results for a session
    public List<Result> getResultsBySessionId(int sessionId) throws SQLException {
        StudentDAO studentDAO = new StudentDAO(connection);
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM result WHERE session_idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Result result = new Result(
                        resultSet.getInt("student_student_number"),
                        resultSet.getInt("session_idsession"),
                        resultSet.getInt("grade"),
                        resultSet.getString("state")
                    );
                    result.setStudent(studentDAO.getStudentByStudentNumber(result.getStudent_student_number()));
                    results.add(result);
                }
            }
        }
        return results;
    }

    public Result getResultByKey(int sessionId, int studentId) throws SQLException {
        StudentDAO studentDAO = new StudentDAO(connection);
        Result result = null;
        String query = "SELECT * FROM result WHERE session_idsession = ? AND student_student_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            preparedStatement.setInt(2, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = new Result(
                        resultSet.getInt("student_student_number"),
                        resultSet.getInt("session_idsession"),
                        resultSet.getInt("grade"),
                        resultSet.getString("state")
                    );
                    result.setStudent(studentDAO.getStudentByStudentNumber(result.getStudent_student_number()));
                }
            }
        }
        return result;
    }
}
