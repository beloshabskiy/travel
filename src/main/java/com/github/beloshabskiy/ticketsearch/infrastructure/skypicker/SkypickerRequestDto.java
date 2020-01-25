package com.github.beloshabskiy.ticketsearch.infrastructure.skypicker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkypickerRequestDto {
    private String flyFrom;
    private String flyTo;
    private String dateFrom;
    private String dateTo;
    private String returnFrom;
    private String returnTo;
    private String currency;
    private Integer limit;
    private Integer maxStopovers;
}
