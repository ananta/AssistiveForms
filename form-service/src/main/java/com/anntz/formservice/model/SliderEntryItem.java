package com.anntz.formservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("SliderEntryItem")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class SliderEntryItem extends FormItem{
    @Column(name="max_value")
    private Integer maxValue;

    @Column(name="min_value")
    private Integer minValue;
}

