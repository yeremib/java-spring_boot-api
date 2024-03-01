package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.ConstantTable;
import com.enigma.wmb_api.constant.TransactionType;
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
    @Enumerated(EnumType.STRING)
    private TransactionType id;

    @Column(name = "description", nullable = false)
    private String description;
}
