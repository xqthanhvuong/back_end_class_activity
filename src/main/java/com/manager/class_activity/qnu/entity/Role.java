package com.manager.class_activity.qnu.entity;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "type_id")
    Type type;  //done

    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp createdAt;

    @Column(nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp updatedAt;

    @Column(nullable = false)
    boolean isDeleted = false;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<AccountRole> accountRoles;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<RolePermission> rolePermissions;

}

