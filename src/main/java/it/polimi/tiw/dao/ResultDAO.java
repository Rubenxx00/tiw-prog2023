package it.polimi.tiw.dao;

import it.polimi.tiw.InvalidValueException;
import it.polimi.tiw.beans.Result;
import it.polimi.tiw.beans.ResultState;
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

    public List<Result> getResultsBySessionId(int sessionId) throws SQLException {
        return this.getResultsBySessionId(sessionId, null);
    }

    public List<Result> getResultsBySessionId(int sessionId, String sortBy, String sortOrder) throws SQLException {
        String query = null;
        if(sortBy != null && !sortBy.isEmpty()){
            query = this.sortResults(sortBy, sortOrder);
        }
        return this.getResultsBySessionId(sessionId, query);
    }


    // get all results for a session
    private List<Result> getResultsBySessionId(int sessionId, String orderBy) throws SQLException {
        StudentDAO studentDAO = new StudentDAO(connection);
        List<Result> results = new ArrayList<>();

        String query = "SELECT * FROM result INNER JOIN student ON student.student_number = result.student_student_number INNER JOIN mydb.user u on student.student_number = u.login" +
                " WHERE session_idsession = ?";

        if(orderBy != null && !orderBy.isEmpty())
            query += orderBy;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Result result = new Result(
                        resultSet.getInt("student_student_number"),
                        resultSet.getInt("session_idsession"),
                        resultSet.getInt("grade"),
                        resultSet.getInt("state"),
                        resultSet.getBoolean("isRefused")
                    );
                    result.setStudent(new Student(
                        resultSet.getInt("student_number"),
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("school"),
                        resultSet.getString("email")
                    ));
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
                        resultSet.getInt("state"),
                        resultSet.getBoolean("isRefused")
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

    // set status for all results in a session
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

    private String sortResults(String sortBy, String sortOrder) {
        String orderBy = " ORDER BY ";
        switch (sortBy) {
            case "studentId":
                orderBy += "student_student_number";
                break;
            case "grade":
                orderBy += "grade";
                break;
            case "name":
                orderBy += "name";
                break;
            case "surname":
                orderBy += "surname";
                break;
            case "email":
                orderBy += "email";
                break;
            case "school":
                orderBy += "school";
                break;
            case "state":
                orderBy += "state";
                break;
            default:
                orderBy = null;
                break;
        }
        if (orderBy != null && sortOrder.equals("desc")) {
            orderBy += " DESC";
        }
        return orderBy;
    }

    // update rows in DB to set ResultState to REFUSED
    public void refuseResult(int sessionId, int studentId) throws SQLException {
        String query = "UPDATE result SET isRefused = ? WHERE session_idsession = ? AND student_student_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, sessionId);
            preparedStatement.setInt(3, studentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean hasNotPublishedResults(int sessionId) throws SQLException {
        String query = "SELECT * FROM result WHERE session_idsession = ? AND state < ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            preparedStatement.setInt(2, ResultState.PUBBLICATO.getValue());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateResults(Integer sessionId, List<Result> results) throws SQLException, InvalidValueException {
        //begin transaction
        try {
            connection.setAutoCommit(false);
            for(Result result : results){
                String query = "UPDATE result SET grade = ? , state = ? WHERE session_idsession = ? AND student_student_number = ?";
                if(result.getGrade() < 0 || result.getGrade() > 31)
                    throw new InvalidValueException("Grade must be between 0 and 30L");
                // TODO: further checks
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, result.getGrade());
                    preparedStatement.setInt(2, result.getGrade() == 0 ? ResultState.NULL.getValue() : ResultState.INSERITO.getValue());
                    preparedStatement.setInt(3, sessionId);
                    preparedStatement.setInt(4, result.getStudent_student_number());
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
            //commit transaction
            connection.commit();
        } catch (SQLException e) {
            //rollback transaction
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
