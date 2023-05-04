package com.innov.workflow.app.dto.core;

import com.innov.workflow.core.domain.BaseEntity;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BaseDTO {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted = false;

    public void fromBaseEntity(BaseEntity entity) {
        setCreatedAt(entity.getCreatedAt());
        setDeleted(entity.isDeleted());
        setUpdatedAt(entity.getUpdatedAt());
    }

    public static BaseDTO fromEntity(BaseEntity entity) {
        BaseDTO dto = new BaseDTO();
        dto.fromBaseEntity(entity);
        return dto;
    }
    
}
