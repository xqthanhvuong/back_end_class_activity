CREATE TABLE Minutes_of_class_activities (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             class_activity_id INT NOT NULL,
                                             created_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                             updated_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP),
                                             last_month_activity TEXT,
                                             this_month_activity TEXT,
                                             teacher_feedback TEXT,
                                             class_feedback TEXT
);
ALTER TABLE Minutes_of_class_activities
    ADD CONSTRAINT fk_class_activity
        FOREIGN KEY (class_activity_id)
            REFERENCES class_activity(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;
