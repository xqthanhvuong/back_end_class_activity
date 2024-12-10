package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.response.AccountHaveRole;
import com.manager.class_activity.qnu.entity.AccountRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mappings({
            @Mapping(source = "account.username", target = "username")
    })
    AccountHaveRole toAccountHaveRole(AccountRole item);
}
