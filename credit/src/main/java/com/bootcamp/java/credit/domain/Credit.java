package com.bootcamp.java.credit.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@ToString
@EqualsAndHashCode(of = { "creditNumber" })
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "credit")
public class Credit {
    @Id
    private String id;
    @NotNull
    private String productCode;
    @NotNull
    private LocalDate creditDate;
    @NotNull
    private String creditNumber;
    @NotNull
    private Float amountRequested;
    private Float accountPending;
    @NotNull
    private Float monthlyFee;
    @NotNull
    private Integer payDay;
    @NotNull
    private ClientDocument client;
    private Boolean active;
}
