package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.NewTableNumReq;
import com.enigma.wmb_api.entity.TableNum;

import java.util.List;

public interface TableNumService {
    TableNum create(NewTableNumReq req);
    TableNum getById(String id);
    List<TableNum> getAll(String name);
    TableNum update(TableNum tableNum);
    void delete(String id);
}
