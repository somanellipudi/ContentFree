package com.bcp.contentfree.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Setter
@Getter
public class DeleteUserRequest {

    @NotNull
    private String userName;

    @NotNull
    private String password;
}
