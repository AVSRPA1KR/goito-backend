package com.goito.springboot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "apparel_types")
public class ApparelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apparelTypeId;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "apparelType", cascade = CascadeType.ALL)
    private List<Size> sizes;

}

