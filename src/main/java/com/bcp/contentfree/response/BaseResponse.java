package com.bcp.contentfree.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseResponse {

    private String responseCode;
    private String responseMessage;
}
