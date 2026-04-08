package dao;  // ✅ CORRECT PACKAGE

import database.DBConnection;
import model.Grade;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private DBConnection dbConnection;

    public StudentDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    // CREATE Student - ✅ CORRECT
    public boolean addStudent(Student student) {
        String userSQL = "INSERT INTO users (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
        String studentSQL = "INSERT INTO students (user_id, student_number, enrollment_year, program) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement studentStmt = null;

        try {
            conn = dbConnection.getConnection();
            userStmt = conn.prepareStatement(userSQL, Statement.RETURN_GENERATED_KEYS);
            studentStmt = conn.prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS);

            // Insert user first
            userStmt.setString(1, student.getFirstName());
            userStmt.setString(2, student.getLastName());
            userStmt.setString(3, student.getEmail());
            userStmt.setString(4, student.getPhone() != null ? student.getPhone() : "");
            userStmt.executeUpdate();

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);

                // Insert student
                studentStmt.setInt(1, userId);
                studentStmt.setString(2, student.getStudentNumber());
                studentStmt.setInt(3, student.getEnrollmentYear());
                studentStmt.setString(4, student.getProgram());
                int result = studentStmt.executeUpdate();

                ResultSet studentRs = studentStmt.getGeneratedKeys();
                if (studentRs.next()) {
                    student.setStudentId(studentRs.getInt(1));
                    student.setId(userId);
                }
                rs.close();
                studentRs.close();
                return result > 0;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error adding student: " + e.getMessage());
            e.printStackTrace(); // For debugging
        } finally {
            // Manual cleanup for better control
            closeResources(conn, userStmt, studentStmt);
        }
        return false;
    }

    // READ All Students - ✅ CORRECT
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.student_number, s.enrollment_year, s.program, " +
                "u.id, u.first_name, u.last_name, u.email, u.phone " +
                "FROM students s JOIN users u ON s.user_id = u.id " +
                "ORDER BY s.student_id ASC";  // 🔥 CHANGED: ASC (1,2,3)

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setStudentNumber(rs.getString("student_number"));
                student.setEnrollmentYear(rs.getInt("enrollment_year"));
                student.setProgram(rs.getString("program"));
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching students: " + e.getMessage());
        }
        return students;
    }

    // READ Student by ID - ✅ CORRECT
    public Student getStudentById(int id) {
        String sql = "SELECT s.*, u.first_name, u.last_name, u.email, u.phone " +
                "FROM students s JOIN users u ON s.user_id = u.id " +
                "WHERE s.student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();

                student.setStudentId(rs.getInt("student_id"));
                student.setStudentNumber(rs.getString("student_number"));
                student.setEnrollmentYear(rs.getInt("enrollment_year"));
                student.setProgram(rs.getString("program"));

                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));

                return student;   // ✅ RETURN SINGLE OBJECT
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching student: " + e.getMessage());
        }

        return null;  // ✅ if not found
    }

    // UPDATE Student - ✅ CORRECT (with transaction)
    public boolean updateStudent(Student student) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction  ← ADVANCED FEATURE!

            String userSQL = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";
            String studentSQL = "UPDATE students SET student_number = ?, enrollment_year = ?, program = ? WHERE student_id = ?";

            try (PreparedStatement userStmt = conn.prepareStatement(userSQL);
                 PreparedStatement studentStmt = conn.prepareStatement(studentSQL)) {

                // Update user
                userStmt.setString(1, student.getFirstName());
                userStmt.setString(2, student.getLastName());
                userStmt.setString(3, student.getEmail());
                userStmt.setString(4, student.getPhone() != null ? student.getPhone() : "");
                userStmt.setInt(5, student.getId());
                int userUpdated = userStmt.executeUpdate();

                // Update student
                studentStmt.setString(1, student.getStudentNumber());
                studentStmt.setInt(2, student.getEnrollmentYear());
                studentStmt.setString(3, student.getProgram());
                studentStmt.setInt(4, student.getStudentId());
                int studentUpdated = studentStmt.executeUpdate();

                if (userUpdated > 0 && studentUpdated > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating student: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
        return false;
    }

    // DELETE Student - ✅ FIXED (MySQL multi-table DELETE)
    public boolean deleteStudent(int studentId) {
        // MySQL multi-table DELETE syntax
        String sql = "DELETE u, s FROM users u " +
                "INNER JOIN students s ON u.id = s.user_id " +
                "WHERE s.student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            int deletedRows = stmt.executeUpdate();

            // Also delete related grades (CASCADE doesn't cover grades)
            String deleteGradesSQL = "DELETE FROM grades WHERE student_id = ?";
            try (PreparedStatement gradesStmt = conn.prepareStatement(deleteGradesSQL)) {
                gradesStmt.setInt(1, studentId);
                gradesStmt.executeUpdate();
            }

            return deletedRows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting student ID " + studentId + ": " + e.getMessage());
        }
        return false;
    }

    // Grade Operations - ✅ CORRECT
    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO grades (student_id, course_code, course_name, grade, credits, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, grade.getStudentId());
            stmt.setString(2, grade.getCourseCode());
            stmt.setString(3, grade.getCourseName());
            stmt.setDouble(4, grade.getGrade());
            stmt.setInt(5, grade.getCredits());
            stmt.setInt(6, grade.getSemester());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error adding grade: " + e.getMessage());
        }
        return false;
    }

    public List<Grade> getGradesByStudentId(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ? ORDER BY semester DESC, course_code";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade();
                    grade.setId(rs.getInt("id"));
                    grade.setStudentId(rs.getInt("student_id"));
                    grade.setCourseCode(rs.getString("course_code"));
                    grade.setCourseName(rs.getString("course_name"));
                    grade.setGrade(rs.getDouble("grade"));
                    grade.setCredits(rs.getInt("credits"));
                    grade.setSemester(rs.getInt("semester"));
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching grades for student " + studentId + ": " + e.getMessage());
        }
        return grades;
    }

    // Helper method for resource cleanup
    private void closeResources(Connection conn, PreparedStatement... stmts) {
        for (PreparedStatement stmt : stmts) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}