package com.thanh.foodOrder.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Table;

import java.time.Instant;

import com.thanh.foodOrder.util.JwtUtil;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)

   private long id;
   @NotBlank(message = "FullName cannot be empty")
   private String fullName;
   @NotBlank(message = "Email cannot be  empty")
   private String email;
   @NotBlank(message = "Password cannot be  empty")
   private String password;
   private String phone;
   private String createdBy;
   private String updatedBy;
   private long point;
   @Column(name = "created_at")
   private Instant createdAt;
   private Instant updatedAt;
   String refreshToken;

   @ManyToOne()
   @JoinColumn(name = "role_id")
   private Role role;

   @PrePersist
   public void handleBeforeCreated() {
      this.createdAt = Instant.now();
      this.createdBy = JwtUtil.getCurrentUserLogin().orElse("");
   }

   @PreUpdate
   public void handleBeforeUpdated() {
      this.updatedBy = JwtUtil.getCurrentUserLogin().orElse("");
      this.updatedAt = Instant.now();
   }
}
