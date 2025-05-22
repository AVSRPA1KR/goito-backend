package com.goito.springboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "sizes", uniqueConstraints = @UniqueConstraint(columnNames = {"apparel_type_id", "size_label"}))
public class Size {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "size_id", updatable = false, nullable = false)
    private UUID sizeId;

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

    public UUID getSizeId() {
        return sizeId;
    }

    public void setSizeId(UUID sizeId) {
        this.sizeId = sizeId;
    }

    public ApparelType getApparelType() {
        return apparelType;
    }

    public void setApparelType(ApparelType apparelType) {
        this.apparelType = apparelType;
    }

    public String getSizeLabel() {
        return sizeLabel;
    }

    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    public BigDecimal getChestCm() {
        return chestCm;
    }

    public void setChestCm(BigDecimal chestCm) {
        this.chestCm = chestCm;
    }

    public BigDecimal getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(BigDecimal lengthCm) {
        this.lengthCm = lengthCm;
    }
}