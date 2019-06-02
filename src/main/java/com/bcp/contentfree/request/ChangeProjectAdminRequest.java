package com.bcp.contentfree.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class ChangeProjectAdminRequest {

    @NonNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String projectName;

    @NonNull
    private String oldAdminUserName;
    @NonNull
    private String oldUserPassword;
    @NonNull
    private String newAdminUserName;
}
