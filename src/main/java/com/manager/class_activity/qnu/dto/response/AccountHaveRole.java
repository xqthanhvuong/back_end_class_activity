package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountHaveRole {
    String username;
    String type;
    String name;
    int id;
}
