package com.thanh.foodOrder.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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

   @ManyToOne()
   @JoinColumn(name = "role_id")
   private Role role;

   @PrePersist
   public void handleBeforeCreated() {
      this.createdAt = Instant.now();
   }

   @PreUpdate
   public void handleBeforeUpdated() {
      this.updatedAt = Instant.now();
   }
}
