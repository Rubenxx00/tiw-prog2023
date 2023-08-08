package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.Teacher;

public class CourseDAO {
    private final Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    // default order: title desc
    public List<Course> findCoursesByTeacherId(int teacherId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course " +
                "INNER JOIN user ON course.teacher = user.login " +
                "WHERE teacher = ? " +
                "ORDER BY title DESC";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, teacherId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setIdcourse(resultSet.getInt("idcourse"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher_idteacher(resultSet.getInt("teacher"));
                    course.setTeacher(new Teacher(
                            resultSet.getInt("login"),
                            resultSet.getString("name"),
                            resultSet.getString("surname"),
                            resultSet.getString("email")
                    ));
                    courses.add(course);
                }
            }
        }
        return courses;
    }
    // find courses by student id
    public List<Course> findCoursesByStudentId(int studentId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course " +
                "INNER JOIN enroll ON course.idcourse = enrollment.course_idcourse " +
                "WHERE enroll.student_student_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setIdcourse(resultSet.getInt("idcourse"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher_idteacher(resultSet.getInt("teacher"));
                    courses.add(course);
                }
            }
        }
        return courses;
    }
    // get by id
    public Course getCourseById(int id) throws SQLException {
        Course course = null;
        String query = "SELECT * FROM course WHERE idcourse = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    course = new Course();
                    course.setIdcourse(resultSet.getInt("idcourse"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher_idteacher(resultSet.getInt("teacher"));
                }
            }
        }
        return course;
    }

    public List<Course> findEnrolledCoursesByStudentId(int login) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course " +
                "INNER JOIN enroll ON course.idcourse = enroll.course_idcourse " +
                "INNER JOIN user ON course.teacher = user.login " +
                "WHERE enroll.student_student_number = ?" +
                "ORDER BY title DESC";;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, login);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course();
                    course.setIdcourse(resultSet.getInt("idcourse"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher_idteacher(resultSet.getInt("teacher"));
                    course.setTeacher(new Teacher(
                            resultSet.getInt("login"),
                            resultSet.getString("name"),
                            resultSet.getString("surname"),
                            resultSet.getString("email")
                    ));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course findCourseBySession(int sessionId) {
        Course course = null;
        String query = "SELECT * FROM course " +
                "INNER JOIN session ON course.idcourse = session.course_idcourse " +
                "WHERE session.idsession = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sessionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    course = new Course();
                    course.setIdcourse(resultSet.getInt("idcourse"));
                    course.setTitle(resultSet.getString("title"));
                    course.setTeacher_idteacher(resultSet.getInt("teacher"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }
}
