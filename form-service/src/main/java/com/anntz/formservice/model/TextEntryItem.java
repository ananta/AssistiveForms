package com.anntz.formservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("TextEntryItem")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class TextEntryItem extends FormItem{
    @Column(name ="placeholder_text")
    private String placeholderText;
}
