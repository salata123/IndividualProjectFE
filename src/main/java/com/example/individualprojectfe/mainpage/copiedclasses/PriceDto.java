package com.example.individualprojectfe.mainpage.copiedclasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private Long id;
    private String currency;
    private String total;
    private String base;
}