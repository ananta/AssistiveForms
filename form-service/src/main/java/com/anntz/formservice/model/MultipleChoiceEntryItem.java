package com.anntz.formservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MultipleChoiceEntryItem")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class MultipleChoiceEntryItem extends FormItem{

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "multiple_choice_options", joinColumns = @JoinColumn(name = "form_item_id"))
//    @Column(name = "options", nullable = false)
    @Column(name = "options")
    private List<String> options = new ArrayList<>();

    @Column(name="multiple_allowed")
    private Boolean multipleAllowed;
}
