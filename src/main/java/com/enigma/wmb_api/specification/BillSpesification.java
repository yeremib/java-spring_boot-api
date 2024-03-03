package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.util.DateUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillSpesification {
    public static Specification<Menu> getSpesification(SearchBillReq request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

//            if (request.getTransDate() != null) {
//                Date tempDate = DateUtil.parseDate(request.getTransDate(), "yyyy-MM-dd");
//                Predicate birthDatePredicate = cb.equal(root.get("birthDate"), tempDate);
//                predicates.add(birthDatePredicate);
//            }


            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
