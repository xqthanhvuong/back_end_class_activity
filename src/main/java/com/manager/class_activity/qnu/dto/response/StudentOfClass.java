package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentOfClass {
    int id;
    String studentCode;;
    String name;
    String gender;
    String email;
    Date birthDate;
    String studentPosition;
}
