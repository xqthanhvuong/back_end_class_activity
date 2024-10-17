CREATE TABLE `type` (
                        `id` INT PRIMARY KEY AUTO_INCREMENT,
                        `name` VARCHAR(255) NOT NULL
);

CREATE TABLE `role` (
                        `id` INT PRIMARY KEY AUTO_INCREMENT,
                        `name` VARCHAR(255) NOT NULL,
                        `type_id` int,
                        `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                        `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                        `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `account_role` (
                                `id` INT PRIMARY KEY AUTO_INCREMENT,
                                `account_id` int NOT NULL,
                                `role_id` int NOT NULL
);

CREATE TABLE `permission` (
                              `id` INT PRIMARY KEY AUTO_INCREMENT,
                              `name` VARCHAR(255) NOT NULL,
                              `type_id` int,
                              `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                              `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `role_permission` (
                                   `id` INT PRIMARY KEY AUTO_INCREMENT,
                                   `role_id` INT NOT NULL,
                                   `permission_id` INT NOT NULL,
                                   `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `account` (
                           `id` INT PRIMARY KEY AUTO_INCREMENT,
                           `username` VARCHAR(255) NOT NULL,
                           `password` VARCHAR(255) NOT NULL,
                           `type_id` int,
                           `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                           `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                           `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `department` (
                              `id` INT PRIMARY KEY AUTO_INCREMENT,
                              `name` VARCHAR(255) NOT NULL,
                              `url_logo` VARCHAR(500),
                              `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                              `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                              `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `course` (
                          `id` INT PRIMARY KEY AUTO_INCREMENT,
                          `name` VARCHAR(255) NOT NULL,
                          `start_year` YEAR NOT NULL,
                          `end_year` YEAR NOT NULL,
                          `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                          `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                          `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `class` (
                         `id` INT PRIMARY KEY AUTO_INCREMENT,
                         `name` VARCHAR(255) NOT NULL,
                         `department_id` INT,
                         `course_id` INT,
                         `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                         `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                         `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `student` (
                           `id` INT PRIMARY KEY AUTO_INCREMENT,
                           `name` VARCHAR(255) NOT NULL,
                           `gender` ENUM ('Male', 'Female', 'Other') NOT NULL,
                           `class_id` INT,
                           `birth_date` DATE,
                           `id_account` INT,
                           `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                           `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                           `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `lecturer` (
                            `id` INT PRIMARY KEY AUTO_INCREMENT,
                            `name` VARCHAR(255) NOT NULL,
                            `gender` ENUM ('Male', 'Female', 'Other') NOT NULL,
                            `degree` VARCHAR(255) NOT NULL,
                            `birth_date` DATE,
                            `phone_number` VARCHAR(15),
                            `email` VARCHAR(255) NOT NULL,
                            `department_id` INT,
                            `id_account` INT,
                            `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                            `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                            `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `academic_advisor` (
                                    `id` INT PRIMARY KEY AUTO_INCREMENT,
                                    `lecturer_id` INT,
                                    `class_id` INT,
                                    `academic_year` VARCHAR(9) NOT NULL,
                                    `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                    `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                    `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `staff` (
                         `id` INT PRIMARY KEY AUTO_INCREMENT,
                         `name` VARCHAR(255) NOT NULL,
                         `birth_date` DATE,
                         `gender` ENUM ('Male', 'Female', 'Other') NOT NULL,
                         `phone_number` VARCHAR(15),
                         `email` VARCHAR(50),
                         `id_account` INT,
                         `department_id` INT,
                         `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                         `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                         `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `activity_guide` (
                                  `id` INT PRIMARY KEY AUTO_INCREMENT,
                                  `name` VARCHAR(255) NOT NULL,
                                  `pdf_url` VARCHAR(500) NOT NULL,
                                  `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                  `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                  `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `department_activity_guide` (
                                             `id` INT PRIMARY KEY AUTO_INCREMENT,
                                             `activity_guide_id` INT NOT NULL,
                                             `department_id` INT NOT NULL,
                                             `pdf_url` VARCHAR(500),
                                             `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                             `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                             `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `student_position` (
                                    `id` INT PRIMARY KEY AUTO_INCREMENT,
                                    `student_id` INT NOT NULL,
                                    `class_id` INT NOT NULL,
                                    `position` ENUM ('ClassLeader', 'ViceLeader', 'Secretary', 'Member') NOT NULL,
                                    `start_date` DATE NOT NULL,
                                    `end_date` DATE,
                                    `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                    `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                    `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `class_activity` (
                                  `id` INT PRIMARY KEY AUTO_INCREMENT,
                                  `class_id` INT NOT NULL,
                                  `leader_id` INT NOT NULL,
                                  `activity_guide_id` INT NOT NULL,
                                  `activity_time` DATETIME NOT NULL,
                                  `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                  `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                  `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `notification` (
                                `id` INT PRIMARY KEY AUTO_INCREMENT,
                                `class_activity_id` INT NOT NULL,
                                `notification_time` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                `message` TEXT NOT NULL,
                                `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `notification_recipient` (
                                          `id` INT PRIMARY KEY AUTO_INCREMENT,
                                          `notification_id` INT NOT NULL,
                                          `recipient_type` ENUM ('Student', 'Advisor') NOT NULL,
                                          `account_id` INT NOT NULL,
                                          `is_read` BOOLEAN DEFAULT false,
                                          `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `attendance_session` (
                                      `id` INT PRIMARY KEY AUTO_INCREMENT,
                                      `class_activity_id` INT NOT NULL,
                                      `attendance_code` VARCHAR(10) NOT NULL,
                                      `start_time` DATETIME NOT NULL,
                                      `end_time` DATETIME NOT NULL,
                                      `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                      `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                      `is_deleted` BOOLEAN DEFAULT false
);

CREATE TABLE `attendance_record` (
                                     `id` INT PRIMARY KEY AUTO_INCREMENT,
                                     `attendance_session_id` INT NOT NULL,
                                     `student_id` INT NOT NULL,
                                     `check_in_time` DATETIME,
                                     `status` ENUM ('Present', 'Absent') DEFAULT 'Absent',
                                     `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                     `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `activity_view` (
                                 `id` INT PRIMARY KEY AUTO_INCREMENT,
                                 `student_id` INT NOT NULL,
                                 `class_activity_id` INT NOT NULL,
                                 `is_readed` BOOLEAN DEFAULT false,
                                 `view_time` TIMESTAMP,
                                 `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                 `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP)
);

ALTER TABLE `role_permission` ADD FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);

ALTER TABLE `role_permission` ADD FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`);

ALTER TABLE `role` ADD FOREIGN KEY (`type_id`) REFERENCES `type` (`id`);

ALTER TABLE `permission` ADD FOREIGN KEY (`type_id`) REFERENCES `type` (`id`);

ALTER TABLE `account` ADD FOREIGN KEY (`type_id`) REFERENCES `type` (`id`);

ALTER TABLE `account_role` ADD FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);

ALTER TABLE `account_role` ADD FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);

ALTER TABLE `class` ADD FOREIGN KEY (`department_id`) REFERENCES `department` (`id`);

ALTER TABLE `class` ADD FOREIGN KEY (`course_id`) REFERENCES `course` (`id`);

ALTER TABLE `student` ADD FOREIGN KEY (`class_id`) REFERENCES `class` (`id`);

ALTER TABLE `student` ADD FOREIGN KEY (`id_account`) REFERENCES `account` (`id`);

ALTER TABLE `lecturer` ADD FOREIGN KEY (`department_id`) REFERENCES `department` (`id`);

ALTER TABLE `lecturer` ADD FOREIGN KEY (`id_account`) REFERENCES `account` (`id`);

ALTER TABLE `academic_advisor` ADD FOREIGN KEY (`lecturer_id`) REFERENCES `lecturer` (`id`);

ALTER TABLE `academic_advisor` ADD FOREIGN KEY (`class_id`) REFERENCES `class` (`id`);

ALTER TABLE `staff` ADD FOREIGN KEY (`id_account`) REFERENCES `account` (`id`);

ALTER TABLE `staff` ADD FOREIGN KEY (`department_id`) REFERENCES `department` (`id`);

ALTER TABLE `department_activity_guide` ADD FOREIGN KEY (`activity_guide_id`) REFERENCES `activity_guide` (`id`);

ALTER TABLE `department_activity_guide` ADD FOREIGN KEY (`department_id`) REFERENCES `department` (`id`);

ALTER TABLE `student_position` ADD FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

ALTER TABLE `student_position` ADD FOREIGN KEY (`class_id`) REFERENCES `class` (`id`);

ALTER TABLE `class_activity` ADD FOREIGN KEY (`class_id`) REFERENCES `class` (`id`);

ALTER TABLE `class_activity` ADD FOREIGN KEY (`leader_id`) REFERENCES `student` (`id`);

ALTER TABLE `class_activity` ADD FOREIGN KEY (`activity_guide_id`) REFERENCES `activity_guide` (`id`);

ALTER TABLE `notification` ADD FOREIGN KEY (`class_activity_id`) REFERENCES `class_activity` (`id`);

ALTER TABLE `notification_recipient` ADD FOREIGN KEY (`notification_id`) REFERENCES `notification` (`id`);

ALTER TABLE `attendance_session` ADD FOREIGN KEY (`class_activity_id`) REFERENCES `class_activity` (`id`);

ALTER TABLE `attendance_record` ADD FOREIGN KEY (`attendance_session_id`) REFERENCES `attendance_session` (`id`);

ALTER TABLE `attendance_record` ADD FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

ALTER TABLE `activity_view` ADD FOREIGN KEY (`student_id`) REFERENCES `student` (`id`);

ALTER TABLE `activity_view` ADD FOREIGN KEY (`class_activity_id`) REFERENCES `class_activity` (`id`);

ALTER TABLE `notification_recipient` ADD FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);
