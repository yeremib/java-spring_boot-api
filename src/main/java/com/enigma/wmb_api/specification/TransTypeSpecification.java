package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.request.SearchTransTypeRequest;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.TransType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransTypeSpecification {
    public static Specification<TransType> getSpesification(SearchTransTypeRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(request.getDescription() != null) {
                Predicate descPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + request.getDescription() + "%");
                predicates.add(descPredicate);
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
