package com.innov.workflow.app.dto;

import lombok.Data;

@Data
public class PaginationDto {
    private Integer pageNumber;

    private Integer pageSize;


    public int getStart() {
        setDefault();
        return (pageNumber - 1) * pageSize;
    }

    private void setDefault() {
        if (pageNumber == null) this.pageNumber = 1;
        if (pageSize == null) this.pageSize = 25;
    }

}
