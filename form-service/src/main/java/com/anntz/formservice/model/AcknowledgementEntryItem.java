package com.anntz.formservice.model;

import jakarta.persistence.Column;

public class InformationEntryItem extends FormItem{

    @Column(name = "information_text")
    private String informationText;

    @Column(name = "required_acknowledgement")
    private Boolean  requiredAcknowledgement;

}
