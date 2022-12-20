package com.bootcamp.java.credit.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel {
    @JsonIgnore
    private String id;
    private String identityDocumentType;
    private String identityDocumentNumber;
    private String name;
    private String lastName;
    private String businessName;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private String clientType;
    private String clientProfile;
}
