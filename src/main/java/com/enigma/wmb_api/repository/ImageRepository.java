package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, String> {
}
