package com.bcp.contentfree.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class LoginRequest {

    @NotNull
    private String userName;

    @NotNull
    private String password;


}
