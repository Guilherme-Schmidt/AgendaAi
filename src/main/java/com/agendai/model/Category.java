package com.agendai.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import com.agendai.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;

    @Builder.Default
    private Boolean active = true;

    // Relationships
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Service> services;
}