package model;

import java.util.List;

public class Student extends User {
    private int studentId;
    private String studentNumber;
    private int enrollmentYear;
    private String program;
    private List<Grade> grades;
    private int semester;

    // Default constructor
    public Student() {}

    // Constructor with all fields
    public Student(String firstName, String lastName, String email, String phone,
                   String studentNumber, int enrollmentYear, String program) {
        super(firstName, lastName, email, phone);
        this.studentNumber = studentNumber;
        this.enrollmentYear = enrollmentYear;
        this.program = program;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public int getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }

    @Override
    public String toString() {
        return String.format("%s | Student#: %s | Program: %s | Year: %d",
                super.toString(), studentNumber, program, enrollmentYear);
    }
}