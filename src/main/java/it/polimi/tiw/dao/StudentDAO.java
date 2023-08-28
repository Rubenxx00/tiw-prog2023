package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

public class StudentDAO {
    private final Connection connection;
    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    // get student by student number
    public Student getStudentByStudentNumber(int studentNumber) throws SQLException {
        Student student = null;
        String query = "SELECT * FROM student" +
                " INNER JOIN user ON student.student_number = user.login" +
                " WHERE student_number = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    student = new Student(
                            resultSet.getInt("student_number"),
                            resultSet.getString("school")
                    );
                    student.setLogin(resultSet.getInt("login"));
                    student.setName(resultSet.getString("name"));
                    student.setSurname(resultSet.getString("surname"));
                    student.setEmail(resultSet.getString("email"));
                }
            }
        }
        return student;
    }

}
