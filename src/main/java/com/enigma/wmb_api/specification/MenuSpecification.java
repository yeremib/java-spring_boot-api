package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MenuSpecification {
    public static Specification<Menu> getSpesification(SearchMenuRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(request.getName() != null) {
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + request.getName() + "%");
                predicates.add(namePredicate);
            }

            if(request.getMinPrice() != null) {
                Predicate minPricePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice());
                predicates.add(minPricePredicate);
            }

            if(request.getMaxPrice() != null) {
                Predicate maxPricePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice());
                predicates.add(maxPricePredicate);
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
