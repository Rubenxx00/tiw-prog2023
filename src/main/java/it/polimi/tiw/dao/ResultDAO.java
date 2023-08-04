package it.polimi.tiw.dao;

import it.polimi.tiw.InvalidValueException;
import it.polimi.tiw.beans.Result;
import it.polimi.tiw.beans.ResultState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                            resultSet.getInt("state")
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
                        resultSet.getInt("state")
                    );
                    result.setStudent(studentDAO.getStudentByStudentNumber(result.getStudent_student_number()));
                }
            }
        }
        return result;
    }

    // update grade in database
    public void updateGrade(int sessionId, int studentId, int grade) throws InvalidValueException, SQLException {
        if(grade < 1 || grade > 31)
            throw new InvalidValueException("Grade must be between 0 and 30");
        String query = "UPDATE result SET grade = ? , state = ? WHERE session_idsession = ? AND student_student_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, grade);
            preparedStatement.setInt(2, ResultState.INSERITO.getValue());
            preparedStatement.setInt(3, sessionId);
            preparedStatement.setInt(4, studentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // update state in database
    public void updateState(int sessionId, int studentId, ResultState state) throws InvalidValueException, SQLException {
        String query = "UPDATE result SET state = ? WHERE session_idsession = ? AND student_student_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, state.getValue());
            preparedStatement.setInt(2, sessionId);
            preparedStatement.setInt(3, studentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // set status for all results in a session, using a transaction
    public void setResultPublished(int sessionId) throws SQLException {
        ResultState state = ResultState.PUBBLICATO;
        String query = "UPDATE result SET state = ? WHERE session_idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, state.getValue());
            preparedStatement.setInt(2, sessionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // check if any result in session is in state NULL
    public boolean hasNullResults(int sessionId) throws SQLException {
        String query = "SELECT * FROM result WHERE session_idsession = ? AND state = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            preparedStatement.setInt(2, ResultState.NULL.getValue());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void sortResults(List<Result> results, String sortBy, String sortOrder) {
        switch (sortBy) {
            case "studentId":
                results.sort(Comparator.comparing(Result::getStudent_student_number));
                break;
            case "grade":
                results.sort(Comparator.comparing(Result::getGrade));
                break;
            case "name":
                results.sort(Comparator.comparing(o -> o.getStudent().getName()));
                break;
            case "surname":
                results.sort(Comparator.comparing(o -> o.getStudent().getSurname()));
                break;
            case "email":
                results.sort(Comparator.comparing(o -> o.getStudent().getEmail()));
                break;
            case "school":
                results.sort(Comparator.comparing(o -> o.getStudent().getSchool()));
                break;
            case "state":
                results.sort(Comparator.comparing(Result::getState));
                break;
            default:
                break;
        }
        if (sortOrder.equals("desc")) {
            Collections.reverse(results);
        }
    }
}
