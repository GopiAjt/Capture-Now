package com.capturenow.dto;

import lombok.Data;

@Data
public class FilterDto {

    private Integer location;

    private Integer sortByCost;

    private Integer sortByRating;
}
