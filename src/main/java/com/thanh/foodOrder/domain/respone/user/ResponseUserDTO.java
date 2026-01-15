package com.thanh.foodOrder.domain.respone.user;



import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

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
