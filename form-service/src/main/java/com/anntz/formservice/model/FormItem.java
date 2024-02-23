package com.anntz.formservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "form_item_type", discriminatorType = DiscriminatorType.STRING)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Table(name = "FormItem")
public abstract class FormItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_item_id")
    private Long id;

    @Column(name = "previousItem")
    private Long previousItem;

    @Column(name = "nextItem")
    private Long nextItem;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "form_item_type", insertable = false, updatable = false)
    private String formItemType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Form form;
}
