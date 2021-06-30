package com.microservice.oauth2.domain.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservice.oauth2.constant.Status;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class UserDto {

    private Integer id;

    private String username;

    private String password;

    private String email;

    private Integer roleId;

    private String roleName;

    private Status status;
}
