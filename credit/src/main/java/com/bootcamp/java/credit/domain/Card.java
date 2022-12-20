package com.bootcamp.java.credit.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@ToString
@EqualsAndHashCode(of = { "cardNumber" })
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "card")
public class Card {
    @Id
    private String id;
    @NotNull
    private String productCode;
    @NotNull
    private String cardNumber;
    @NotNull
    private String cardCompany;
    private Float amountOwed;
    private Float amountAvailable;
    @NotNull
    private Float amountMax;
    @NotNull
    private Integer payDay;
    @NotNull
    private LocalDate openingDate;
    @NotNull
    private ClientDocument client;
    private Boolean active;
}
