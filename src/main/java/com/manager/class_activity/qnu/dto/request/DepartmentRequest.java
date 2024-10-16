package com.manager.class_activity.qnu.dto.request;

import com.manager.class_activity.qnu.exception.ErrorConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {
    @NotBlank(message = ErrorConstant.NAME_INVALID)
    String name;
    String urlLogo;
}
