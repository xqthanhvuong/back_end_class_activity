package com.manager.class_activity.qnu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {
    @NotBlank(message = "Class name cannot be blank")
    String name;
    int departmentId;
    int courseId;
    String durationYears;
}
