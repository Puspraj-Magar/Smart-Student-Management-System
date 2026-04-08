import service.StudentService;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Student Management System...");

        StudentService studentService = new StudentService();
        studentService.start();

        // Close DB connection on exit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}