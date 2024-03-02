package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.SearchTransTypeRequest;
import com.enigma.wmb_api.entity.TransType;

import java.util.List;

public interface TransTypeService {

    TransType getById(String id);
    List<TransType> getAll(SearchTransTypeRequest request);
    TransType update(TransType transType);

}
