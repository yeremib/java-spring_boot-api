package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.dto.request.SearchBillReq;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.util.DateUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillSpesification {
    public static Specification<Bill> getSpesification(SearchBillReq request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTransType() != null) {
                TransactionType transactionType = TransactionType.valueOf(request.getTransType());
                Predicate transTypePredicate = criteriaBuilder.equal(root.get("transType"), transactionType);
                predicates.add(transTypePredicate);
            }

            if (request.getAfterDate() != null) {
                Date tempDate = DateUtil.parseDate(request.getAfterDate(), "yyyy-MM-dd");
                Predicate afterDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("transDate"), tempDate);
                predicates.add(afterDatePredicate);
            }

            if (request.getBeforeDate() != null) {
                Date tempDate = DateUtil.parseDate(request.getBeforeDate(), "yyyy-MM-dd");
                Predicate beforeDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("transDate"), tempDate);
                predicates.add(beforeDatePredicate);
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
