package com.anntz.formservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("AcknowledgementEntryItem")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class AcknowledgementEntryItem extends FormItem{

    @Column(name = "information_text")
    private String informationText;

    @Column(name = "required_acknowledgement")
    private Boolean  requiredAcknowledgement;

}
