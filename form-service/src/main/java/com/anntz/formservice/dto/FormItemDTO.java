package com.anntz.formservice.dto;

import com.anntz.formservice.constants.ValidationMessages;
import com.anntz.formservice.validation.ConditionalValidation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConditionalValidation(selected = "formItemType", values = {"TEXT_ENTRY"},
        required = {"placeholderText"},
        message = ValidationMessages.TEXT_ENTRY_VALIDATION_MESSAGE
)
@ConditionalValidation(selected = "formItemType", values = {"SLIDER_ENTRY"},
        required = {"minValue", "maxValue"},
        message = ValidationMessages.SLIDER_ENTRY_VALIDATION_MESSAGE
)
@ConditionalValidation(selected = "formItemType", values = {"MULTIPLE_CHOICE_ENTRY"},
        required = {"multipleAllowed", "options"},
        message = ValidationMessages.MULTIPLE_CHOICE_ENTRY_VALIDATION_MESSAGE
)
@ConditionalValidation(selected = "formItemType", values = {"ACKNOWLEDGEMENT_ENTRY"},
        required = {"informationText", "requiredAcknowledgement"},
        message = ValidationMessages.ACKNOWLEDGEMENT_ENTRY_VALIDATION_MESSAGE
)
public class FormItemDTO {
    private Long id;
    private Long formId;
    private Long previousFormItemId;
    private Long nextFormItemId;
    @NotNull(message = "title is a required field")
    private String title;
    private String description;
    @NotNull(message = "formItemType is a required field")
    private String formItemType;
    private String placeholderText;
    private Integer maxLength;
    private  Integer maxValue;
    private Integer minValue;
    private Boolean multipleAllowed;
    private List<String> options;
    private String informationText;
    private Boolean requiredAcknowledgement;

}
