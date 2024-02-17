package com.anntz.formservice.enums;

public enum FormItemEntryType {
    TEXT_ENTRY("TEXT_ENTRY"),
    MULTIPLE_CHOICE_ENTRY("MULTIPLE_CHOICE_ENTRY"),
    SLIDER_ENTRY("SLIDER_ENTRY"),
    INFORMATION_ENTRY("INFORMATION_ENTRY"),
    ACKNOWLEDGEMENT_ENTRY("ACKNOWLEDGEMENT_ENTRY");

    public final String type;

    FormItemEntryType(String type){
        this.type = type;
    }
}
