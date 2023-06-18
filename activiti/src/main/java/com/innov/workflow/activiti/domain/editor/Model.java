package com.innov.workflow.activiti.domain.editor;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "ACT_DE_MODEL")
@Data
@NoArgsConstructor
public class Model extends AbstractModel {
    @Column(name = "thumbnail")
    @Lob
    private byte[] thumbnail;

}
