package com.thanh.foodOrder.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseLoginDTO {
    private long id;
    private String email;
    private String fullname;
    private Role role;
}
