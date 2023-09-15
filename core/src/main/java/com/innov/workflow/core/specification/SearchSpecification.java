package com.innov.workflow.core.specification;


import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecification<T> {
    public Specification<T> searchByFields(
            String[] fieldNames,
            String[] searchTerms,
            String[] notEqualFieldNames,
            String[] notEqualValues) {

        return (root, query, criteriaBuilder) -> {
            if (fieldNames == null || fieldNames.length == 0) {
                return criteriaBuilder.conjunction();
            } else {
                List<Predicate> predicates = new ArrayList<>();

                for (int i = 0; i < fieldNames.length; i++) {
                    String fieldName = fieldNames[i];
                    String searchTerm = "%" + searchTerms[i] + "%";
                    Path<String> fieldPath = root.get(fieldName);
                    Predicate likePredicate = criteriaBuilder.like(fieldPath, searchTerm);
                    predicates.add(likePredicate);
                }

                if (notEqualFieldNames != null && notEqualValues != null) {
                    for (int i = 0; i < notEqualFieldNames.length; i++) {
                        String notEqualFieldName = notEqualFieldNames[i];
                        String notEqualValue = notEqualValues[i];
                        Path<String> notEqualPath = root.get(notEqualFieldName);
                        Predicate notEqualPredicate = criteriaBuilder.notEqual(notEqualPath, notEqualValue);
                        predicates.add(notEqualPredicate);
                    }
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
