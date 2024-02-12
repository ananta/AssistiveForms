package com.anntz.formservice.dto;

import com.anntz.formservice.enums.FormItemEntryType;
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
@ConditionalValidation(selected = "kind", values = {FormItemEntryType.TEXT_ENTRY},
        required = {"placeholderText", "maxLength"},
        message = "placeholderText & maxLength is required for TextEntryItem"
)
@ConditionalValidation(selected = "kind", values = {FormItemEntryType.SLIDER_ENTRY},
        required = {"minValue", "maxValue"},
        message = "minValue & maxValue is required for SliderEntryItem"
)
@ConditionalValidation(selected = "kind", values = {FormItemEntryType.MULTIPLE_CHOICE_ENTRY},
        required = {"maxLength", "multipleAllowed", "options"},
        message = "options, maxLength & multipleAllowed is required for SliderEntryItem"
)
@ConditionalValidation(selected = "kind", values = {FormItemEntryType.ACKNOWLEDGEMENT_ENTRY},
        required = {"informationText", "requiredAcknowledgement"},
        message = "requiredAcknowledgement is required for SliderEntryItem"
)
public class FormItemDTO {
    @NotNull(message = "formId is a required field")
    private Long formId;
    private Long previousFormItemId;
    private Long nextFormItemId;
    @NotNull(message = "title is a required field")
    private String title;
    private String description;
    @NotNull(message = "formItemType is a required field")
    private String formItemType;
    private Object formItemTypeAttributes;
    // TEXTEntryFormItem
    private String placeholderText;
    private String maxLength;
    // SliderEntryFormItem
    private  Integer maxValue;
    private Integer minValue;
    // MultipleChoiceEntryItem
    private Boolean multipleAllowed;
    private List<String> options;

}
