package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.TransType;
import com.enigma.wmb_api.repository.TransTypeRepository;
import com.enigma.wmb_api.service.TransTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransTypeServiceImpl implements TransTypeService {

    private final TransTypeRepository transTypeRepository;

    @Override
    public TransType getById(String id) {
        Optional<TransType> transType = transTypeRepository.findById(id);
        if(transType.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction type not found");
        return transType.get();
    }

    @Override
    public List<TransType> getAll() {
        return transTypeRepository.findAll();
    }

    @Override
    public TransType update(TransType transType) {
        getById(transType.getId().name());
        return transTypeRepository.saveAndFlush(transType);
    }
}
