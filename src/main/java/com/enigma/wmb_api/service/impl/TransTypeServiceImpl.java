package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransactionType;
import com.enigma.wmb_api.dto.request.SearchTransTypeRequest;
import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.repository.TransTypeRepository;
import com.enigma.wmb_api.service.TransTypeService;
import com.enigma.wmb_api.specification.TransTypeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransTypeServiceImpl implements TransTypeService {

    private final TransTypeRepository transTypeRepository;

    @Transactional(readOnly = true)
    @Override
    public TransType getById(String id) {
        TransactionType transactionType = TransactionType.valueOf(id);
        Optional<TransType> transType = transTypeRepository.findById(transactionType);
        if(transType.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction type not found");
        return transType.get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransType> getAll(SearchTransTypeRequest request) {
        Specification<TransType> specification = TransTypeSpecification.getSpesification(request);
        return transTypeRepository.findAll(specification);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransType update(TransType transType) {
        getById(transType.getId().name());
        return transTypeRepository.saveAndFlush(transType);
    }
}
