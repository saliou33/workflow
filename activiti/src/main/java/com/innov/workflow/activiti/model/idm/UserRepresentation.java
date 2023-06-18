package com.innov.workflow.activiti.model.idm;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserRepresentation extends AbstractRepresentation {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String fullName;
    protected List<GroupRepresentation> groups = new ArrayList();


    public UserRepresentation(User user) {
        if (user != null) {
            this.setId(user.getId());
            this.setFirstName(user.getFirstName());
            this.setLastName(user.getLastName());
            this.setFullName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""));
            this.setEmail(user.getEmail());
        }

    }
}
