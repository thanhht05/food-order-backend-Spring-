package com.thanh.foodOrder.domain.respone.user;

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
    private RoleUser roleUser;

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
        private String name;
    }

}
