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
public class LecturerRequest {
    @NotBlank(message = "Lecturer name cannot be blank")
    String name;
    GenderEnum gender;
    String degree;
    Date birthDate;
    String phoneNumber;
    String email;
    int departmentId;
}
