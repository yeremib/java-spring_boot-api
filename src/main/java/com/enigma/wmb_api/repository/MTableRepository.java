package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.TableNum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MTableRepository extends JpaRepository<TableNum, String> {
}
