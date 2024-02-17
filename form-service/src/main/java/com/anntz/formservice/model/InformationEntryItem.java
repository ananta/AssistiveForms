package com.anntz.formservice.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("InformationEntryItem")
@AllArgsConstructor
@SuperBuilder
@Data
public class InformationEntryItem extends FormItem{

}
