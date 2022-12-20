package com.bootcamp.java.credit.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardModel {
    @JsonIgnore
    private String id;
    @NotBlank(message = "Product Code cannot be null or empty")
    private String productCode;
    @NotBlank(message = "Card Number cannot be null or empty")
    private String cardNumber;
    @NotBlank(message = "Card Company cannot be null or empty")
    private String cardCompany;
    private Float amountOwed;
    private Float amountAvailable;
    private Float amountMax;
    private Integer payDay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate openingDate;
    @NotNull(message = "Client cannot be null or empty")
    private ClientDocumentModel client;
    private Boolean active;
}
