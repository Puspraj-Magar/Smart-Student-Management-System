USE student_management;

DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       phone VARCHAR(15),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE students (
                          student_id INT PRIMARY KEY AUTO_INCREMENT,
                          user_id INT,
                          student_number VARCHAR(20) UNIQUE NOT NULL,
                          enrollment_year YEAR NOT NULL,
                          program VARCHAR(100) NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE grades (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        student_id INT NOT NULL,
                        course_code VARCHAR(10) NOT NULL,
                        course_name VARCHAR(100) NOT NULL,
                        grade DECIMAL(4,2) CHECK (grade >= 0 AND grade <= 10),
                        credits INT DEFAULT 3 CHECK (credits BETWEEN 1 AND 5),
                        semester INT CHECK (semester BETWEEN 1 AND 8),
                        FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

INSERT INTO users (first_name, last_name, email, phone) VALUES
    ('Sample', 'User', 'sample@test.com', '0000000000');

INSERT INTO students (user_id, student_number, enrollment_year, program) VALUES
    (1, 'SAMPLE001', 2023, 'Sample Program');