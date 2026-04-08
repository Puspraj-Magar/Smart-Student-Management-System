package service;

import dao.StudentDAO;
import model.Grade;
import model.Student;

import java.util.List;
import java.util.Scanner;

public class StudentService {
    private StudentDAO studentDAO;
    private Scanner scanner;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        // Suppress reconnection spam
        System.setProperty("java.util.logging.ConsoleHandler.level", "WARNING");
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("\n=== 🎓 Student Management System ===");
        System.out.println("1. ➕ Add Student");
        System.out.println("2. 📋 View All Students");
        System.out.println("3. 👤 View Student Details");
        System.out.println("4. ✏️  Update Student");
        System.out.println("5. 🗑️  Delete Student");
        System.out.println("6. 📝 Add Grade");
        System.out.println("7. 📚 View Student Grades");
        System.out.println("0. 🚪 Exit");
        System.out.print("Choose an option: ");
    }

    public void start() {
        int choice;
        do {
            displayMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> viewStudentDetails();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> addGrade();
                case 7 -> viewStudentGrades();
                case 0 -> System.out.println("👋 Thank you for using Student Management System!");
                default -> System.out.println("❌ Invalid option! Please try again.");
            }
            System.out.println(); // Add spacing
        } while (choice != 0);

        // Close scanner
        scanner.close();
    }

    private void addStudent() {
        String firstName = "";
        String lastName = "";
        String email = "";          // 🔥 Declare at top
        String phone = "";
        String studentNumber = "";  // 🔥 Declare at top
        int year = 0;
        String program = "";

        try {
            System.out.print("Enter first name: ");
            firstName = scanner.nextLine().trim();

            System.out.print("Enter last name: ");
            lastName = scanner.nextLine().trim();

            System.out.print("Enter email: ");
            email = scanner.nextLine().trim();     // 🔥 Now accessible in catch

            System.out.print("Enter phone: ");
            phone = scanner.nextLine().trim();

            System.out.print("Enter student number: ");
            studentNumber = scanner.nextLine().trim();  // 🔥 Now accessible in catch

            System.out.print("Enter enrollment year (YYYY): ");
            year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter program: ");
            program = scanner.nextLine().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    studentNumber.isEmpty() || program.isEmpty()) {
                System.out.println("❌ All fields REQUIRED!");
                return;
            }
            if (!email.contains("@")) {
                System.out.println("❌ Invalid email format!");
                return;
            }
            if (year < 2000 || year > 2100) {
                System.out.println("❌ Invalid enrollment year!");
                return;
            }

            Student student = new Student(firstName, lastName, email, phone, studentNumber, year, program);

            if (studentDAO.addStudent(student)) {
                System.out.println("✅ Student added! ID: " + student.getStudentId());
            } else {
                System.out.println("❌ Add failed - check duplicates");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format!");
        } catch (Exception e) {
            // 🔥 NOW studentNumber & email are accessible!
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("duplicate entry")) {
                if (msg.contains("student_number")) {
                    System.out.println("❌ Student number '" + studentNumber + "' already exists!");
                } else if (msg.contains("email")) {
                    System.out.println("❌ Email '" + email + "' already exists!");
                }
                System.out.println("💡 Check Option 2 or delete existing student");
            } else {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private void viewAllStudents() {
        List<Student> students = studentDAO.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("📭 No students found.");
            return;
        }
        System.out.println("\n📋 All Students (" + students.size() + "):");
        System.out.println("=".repeat(80));
        students.forEach(student ->
                System.out.printf("%-5d | %-15s | %-20s | %-12s | %s%n",
                        student.getStudentId(),
                        student.getStudentNumber(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getProgram(),
                        student.getEnrollmentYear()
                )
        );
        System.out.println("=".repeat(80));
    }

    private void viewStudentDetails() {
        System.out.print("Enter student ID (number, not student number): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
            if (id <= 0) {
                System.out.println("❌ ID must be positive number!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Enter valid number (1, 2, etc.)");
            return;
        }

        Student student = studentDAO.getStudentById(id);
        if (student != null) {
            System.out.println("\n👤 Student Details:");
            System.out.println("=".repeat(50));
            System.out.printf("Student ID: %d%n", student.getStudentId());
            System.out.printf("Student #: %s%n", student.getStudentNumber());
            System.out.printf("Name: %s %s%n", student.getFirstName(), student.getLastName());
            System.out.printf("Email: %s%n", student.getEmail());
            System.out.printf("Phone: %s%n", student.getPhone());
            System.out.printf("Program: %s%n", student.getProgram());
            System.out.printf("Year: %d%n", student.getEnrollmentYear());
            System.out.println("=".repeat(50));
        } else {
            System.out.println("❌ Student not found with ID: " + id);
            System.out.println("💡 Use option 2 to see available IDs");
        }
    }

    private void updateStudent() {
        System.out.print("Enter student ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format!");
            return;
        }

        Student student = studentDAO.getStudentById(id);
        if (student == null) {
            System.out.println("❌ Student not found!");
            return;
        }

        System.out.println("\nLeave blank to keep current value:");
        System.out.printf("Current: %s %s | New first name: ",
                student.getFirstName(), student.getLastName());
        String firstName = readLineOrDefault(student.getFirstName());

        System.out.printf("Current: %s | New last name: ", student.getLastName());
        String lastName = readLineOrDefault(student.getLastName());

        System.out.printf("Current: %s | New email: ", student.getEmail());
        String email = readLineOrDefault(student.getEmail());

        System.out.printf("Current: %s | New phone: ", student.getPhone());
        String phone = readLineOrDefault(student.getPhone());

        System.out.printf("Current: %s | New student number: ", student.getStudentNumber());
        String studentNumber = readLineOrDefault(student.getStudentNumber());

        System.out.printf("Current: %d | New enrollment year: ", student.getEnrollmentYear());
        String yearInput = scanner.nextLine().trim();
        int year = yearInput.isEmpty() ? student.getEnrollmentYear() : Integer.parseInt(yearInput);

        System.out.printf("Current: %s | New program: ", student.getProgram());
        String program = readLineOrDefault(student.getProgram());

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setStudentNumber(studentNumber);
        student.setEnrollmentYear(year);
        student.setProgram(program);

        if (studentDAO.updateStudent(student)) {
            System.out.println("✅ Student updated successfully!");
        } else {
            System.out.println("❌ Failed to update student");
        }
    }

    private String readLineOrDefault(String defaultValue) {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private void deleteStudent() {
        System.out.print("Enter student ID to delete: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());

            // Confirmation
            Student student = studentDAO.getStudentById(id);
            if (student == null) {
                System.out.println("❌ Student not found!");
                return;
            }

            System.out.printf("⚠️  Are you sure you want to delete %s %s? (y/N): ",
                    student.getFirstName(), student.getLastName());
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                if (studentDAO.deleteStudent(id)) {
                    System.out.println("✅ Student deleted successfully!");
                } else {
                    System.out.println("❌ Failed to delete student");
                }
            } else {
                System.out.println("❌ Deletion cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format!");
        }
    }

    private void addGrade() {
        try {
            System.out.print("Enter student ID: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());

            // Check if student exists
            if (studentDAO.getStudentById(studentId) == null) {
                System.out.println("❌ Student not found!");
                return;
            }

            // Course details
            System.out.print("Enter course code (e.g., CS101): ");
            String courseCode = scanner.nextLine().trim();

            System.out.print("Enter course name: ");
            String courseName = scanner.nextLine().trim();

            if (courseCode.isEmpty() || courseName.isEmpty()) {
                System.out.println("❌ Course code and name cannot be empty!");
                return;
            }

            // Grade input
            System.out.print("Enter grade (0.0 - 10.0): ");
            double grade = Double.parseDouble(scanner.nextLine().trim());

            if (grade < 0.0 || grade > 10.0) {
                System.out.println("❌ Grade must be between 0.0 and 10.0!");
                return;
            }

            // Credits input
            System.out.print("Enter credits (default 3): ");
            String creditsInput = scanner.nextLine().trim();
            int credits = creditsInput.isEmpty() ? 3 : Integer.parseInt(creditsInput);

            if (credits < 1 || credits > 5) {
                System.out.println("❌ Credits must be between 1 and 5!");
                return;
            }

            // ✅ Semester as INTEGER (1–8)
            System.out.print("Enter semester (1-8): ");
            int semester = Integer.parseInt(scanner.nextLine().trim());

            if (semester < 1 || semester > 8) {
                System.out.println("❌ Semester must be between 1 and 8!");
                return;
            }

            // Create Grade object
            Grade gradeObj = new Grade(
                    studentId,
                    courseCode,
                    courseName,
                    grade,
                    credits,
                    semester   // 🔥 now int
            );

            // Save to DB
            if (studentDAO.addGrade(gradeObj)) {
                System.out.println("✅ Grade added successfully!");
            } else {
                System.out.println("❌ Failed to add grade");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format! Please enter numeric values correctly.");
        } catch (Exception e) {
            System.out.println("❌ Error adding grade: " + e.getMessage());
        }
    }

    private void viewStudentGrades() {
        System.out.print("Enter student ID: ");
        int studentId;
        try {
            studentId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format!");
            return;
        }

        List<Grade> grades = studentDAO.getGradesByStudentId(studentId);
        if (grades.isEmpty()) {
            System.out.println("📭 No grades found for this student.");
            return;
        }

        System.out.println("\n📚 Student Grades (" + grades.size() + "):");
        System.out.println("=".repeat(90));
        System.out.printf("%-8s | %-10s | %-25s | %-6s | %-8s | %-12s%n",
                "ID", "Course", "Name", "Grade", "Credits", "Semester");
        System.out.println("-".repeat(90));

        for (Grade grade : grades) {
            System.out.printf("%-8d | %-10s | %-25s | %-6.2f | %-8d | %-12s%n",
                    grade.getId(),
                    grade.getCourseCode(),
                    grade.getCourseName(),
                    grade.getGrade(),
                    grade.getCredits(),
                    grade.getSemester()
            );
        }
        System.out.println("=".repeat(90));

        // Calculate GPA
        double totalGradePoints = grades.stream()
                .mapToDouble(g -> g.getGrade() * g.getCredits())
                .sum();
        int totalCredits = grades.stream().mapToInt(Grade::getCredits).sum();
        double gpa = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
        System.out.printf("📊 GPA: %.2f | Total Credits: %d | Grade Points: %.2f%n",
                gpa, totalCredits, totalGradePoints);
    }
}