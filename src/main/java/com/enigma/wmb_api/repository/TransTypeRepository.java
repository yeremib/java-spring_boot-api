package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.entity.TransType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransTypeRepository extends JpaRepository<TransType, TransactionType>, JpaSpecificationExecutor<TransType> {
}

