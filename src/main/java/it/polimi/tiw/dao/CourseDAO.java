package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;

public class CourseDAO {
    private final Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Course> findCoursesByTeacherId(int teacherId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course WHERE teacherId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, teacherId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setIdcourse(resultSet.getInt("id"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher(resultSet.getInt("teacher"));
                    courses.add(course);
                }
            }
        }
        return courses;
    }    
    // get by id
    public Course getCourseById(int id) throws SQLException {
        Course course = null;
        String query = "SELECT * FROM course WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    course = new Course();
                    course.setIdcourse(resultSet.getInt("id"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher(resultSet.getInt("teacher"));
                }
            }
        }
        return course;
    }
}
