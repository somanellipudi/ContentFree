package com.bcp.contentfree.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

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
