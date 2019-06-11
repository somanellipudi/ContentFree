package com.bcp.contentfree.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RevokeProjectAccessRequest {

    @NotNull
    private String userName;
    @NotNull
    private String projectName;
}
