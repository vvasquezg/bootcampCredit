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
public class CreditModel {
    @JsonIgnore
    private String id;
    @NotBlank(message = "Product Code cannot be null or empty")
    private String productCode;
    @NotNull(message = "Credit Date cannot be null or empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creditDate;
    @NotBlank(message = "Credit Number cannot be null or empty")
    private String creditNumber;
    @NotNull(message = "Amount Requested cannot be null or empty")
    private Float amountRequested;
    private Float accountPending;
    @NotNull(message = "Monthly Fee cannot be null or empty")
    private Float monthlyFee;
    @NotNull(message = "Pay Day cannot be null or empty")
    private Integer payDay;
    @NotNull(message = "Client cannot be null or empty")
    private ClientDocumentModel client;
    private Boolean active;
}
