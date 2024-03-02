package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.NewTableNumReq;
import com.enigma.wmb_api.entity.TableNum;
import com.enigma.wmb_api.repository.TableNumRepository;
import com.enigma.wmb_api.service.TableNumService;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableNumServiceImpl implements TableNumService {
    private  final TableNumRepository tableNumRepository;
    private final ValidationUtil validationUtil;


    @Override
    public TableNum create(NewTableNumReq req) {
        validationUtil.validate(req);
        TableNum tableNum = TableNum.builder()
                .name(req.getName())
                .build();
        return tableNumRepository.saveAndFlush(tableNum);
    }

    @Override
    public TableNum getById(String id) {
        Optional<TableNum> tableNum = tableNumRepository.findById(id);
        if(tableNum.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "table name not found");
        return tableNum.get();
    }

    @Override
    public List<TableNum> getAll(String name) {
        if(name != null) return tableNumRepository.findAllByNameIgnoreCaseLike("%"+name+"%");
        return tableNumRepository.findAll();
    }

    @Override
    public TableNum update(TableNum tableNum) {
        getById(tableNum.getId());
        return tableNumRepository.saveAndFlush(tableNum);
    }

    @Override
    public void delete(String id) {
        TableNum currTableNum = getById(id);
        tableNumRepository.delete(currTableNum);
    }
}
