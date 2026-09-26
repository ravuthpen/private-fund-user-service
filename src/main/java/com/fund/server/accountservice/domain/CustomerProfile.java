package com.fund.server.accountservice.domain;

import com.fund.server.accountservice.domain.emuns.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Setter
@Table("customer_profile")
@Data
public class CustomerProfile {
    @Id
    private UUID id;
    private UUID userAccountId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String nationalityNumber;
    private String passportNumber;
    private LocalDate dateOfBirth;
    private String email;
    private List<String> photoProfile;
    private List<String> photoObjectKey;
    private Instant createdAt;
    private Instant updatedAt;
}
