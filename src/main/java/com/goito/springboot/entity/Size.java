package com.goito.springboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "sizes", uniqueConstraints = @UniqueConstraint(columnNames = {"apparel_type_id", "size_label"}))
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sizeId;

    @ManyToOne
    @JoinColumn(name = "apparel_type_id", nullable = false)
    @JsonProperty("apparel_type")
    private ApparelType apparelType;

    @Column(nullable = false, length = 10)
    @JsonProperty("size_label")
    private String sizeLabel;

    @Column(precision = 5, scale = 2)
    @JsonProperty("chest_cm")
    private BigDecimal chestCm;

    @Column(precision = 5, scale = 2)
    @JsonProperty("length_cm")
    private BigDecimal lengthCm;

}
