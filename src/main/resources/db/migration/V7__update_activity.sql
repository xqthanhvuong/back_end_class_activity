

ALTER TABLE `class_activity`
DROP FOREIGN KEY `fk_class_activity_activity`;

ALTER TABLE `department_activity_guide`
DROP FOREIGN KEY `department_activity_guide_ibfk_1`;

DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
                            `id` INT PRIMARY KEY AUTO_INCREMENT,
                            `name` VARCHAR(255) NOT NULL,
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            `is_deleted` BOOLEAN DEFAULT false
);

ALTER TABLE `class_activity`
DROP COLUMN `activity_id`;

ALTER TABLE `class_activity`
    ADD COLUMN `activity_id` INT NOT NULL;


ALTER TABLE `class_activity`
    ADD CONSTRAINT `fk_class_activity_activity`
        FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`);



ALTER TABLE `department_activity_guide`
DROP COLUMN `activity_id`;

ALTER TABLE `department_activity_guide`
    ADD COLUMN `activity_id` INT NOT NULL;



ALTER TABLE `department_activity_guide`
    ADD CONSTRAINT `department_activity_guide_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`);


ALTER TABLE `department_activity_guide`
DROP COLUMN `activity_guide_id`;



ALTER TABLE `activity_guide`
    ADD COLUMN `activity_id` INT NOT NULL;

ALTER TABLE `activity_guide`
    ADD CONSTRAINT `activity_guide_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`);
