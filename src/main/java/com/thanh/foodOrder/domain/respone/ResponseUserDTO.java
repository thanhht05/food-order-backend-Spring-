package com.thanh.foodOrder.domain.respone;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResponseUserDTO {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private long point;
    private Instant createdAt;

}
