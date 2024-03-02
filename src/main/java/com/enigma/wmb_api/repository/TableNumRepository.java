package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.TableNum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableNumRepository extends JpaRepository<TableNum, String> {

    List<TableNum> findAllByNameIgnoreCaseLike(String name);
}
