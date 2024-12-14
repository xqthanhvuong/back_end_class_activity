package com.manager.class_activity.qnu.entity;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.HashCodeExclude;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "role_permission")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role; //done

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    Permission permission; //done

    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp createdAt;
}

