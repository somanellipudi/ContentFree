package com.bcp.contentfree.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ApiModel
public class ChangeProjectAdminRequest {

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String projectName;

    @NotNull
    private String oldAdminUserName;
    @NotNull
    private String oldUserPassword;
    @NotNull
    private String newAdminUserName;
}
