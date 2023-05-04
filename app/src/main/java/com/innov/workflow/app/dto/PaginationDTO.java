package com.innov.workflow.app.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PaginationDTO {
    @NotNull(message = "pageNumber: the number of the page is required")
    private Integer pageNumber;
    @NotNull(message = "pageSize: the size of the page is required")
    private Integer pageSize;
}
