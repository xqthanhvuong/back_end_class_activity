package com.manager.class_activity.qnu.dto.request;

import com.manager.class_activity.qnu.entity.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {
    @NotBlank(message = "Student code cannot be blank")
    String studentCode;

    @NotBlank(message = "Student name cannot be blank")
    String name;
    Date birthDate;
    GenderEnum gender;
    int classId;
}
