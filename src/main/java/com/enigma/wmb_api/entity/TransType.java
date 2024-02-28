package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.TRANSTYPE)
public class TransType {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "description", nullable = false)
    private String description;
}
