package model;

public class Grade {
    private int id;
    private int studentId;
    private String courseCode;
    private String courseName;
    private double grade;
    private int credits;
    private int semester;
    // Constructors
    public Grade() {}

    public Grade(int studentId, String courseCode, String courseName, double grade, int credits, int semester) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.grade = grade;
        this.credits = credits;
        this.semester = semester;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    @Override
    public String toString() {
        return String.format("%s - %s (%.2f) [%d credits] - %s",
                courseCode, courseName, grade, credits, semester);
    }

    public void setSemester(String semester) {

    }
}