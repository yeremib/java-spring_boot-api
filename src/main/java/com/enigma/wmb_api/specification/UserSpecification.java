package com.enigma.wmb_api.specification;


import com.enigma.wmb_api.dto.request.SearchUserRequest;
import com.enigma.wmb_api.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> getSpesification(SearchUserRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(request.getName() != null) {
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + request.getName() + "%");
                predicates.add(namePredicate);
            }

            if(request.getPhoneNumber() != null) {
                Predicate phoneNumberPredicate = criteriaBuilder.like(root.get("phoneNumber"),
                "%" + request.getPhoneNumber() + "%");
                predicates.add(phoneNumberPredicate);
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
