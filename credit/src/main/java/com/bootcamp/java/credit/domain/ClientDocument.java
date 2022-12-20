package com.bootcamp.java.credit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDocument {
    @NotNull
    private String identityDocumentType;
    @NotNull
    private String identityDocumentNumber;
}
