package com.anntz.formservice;

import static com.anntz.formservice.constants.ValidationMessages.*;
import com.anntz.formservice.dto.FormItemDTO;
import com.anntz.formservice.enums.FormItemEntryType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FormItemConditionalValidationTests {

    private Validator validator;
    private FormItemDTO formItemDTO;

    @BeforeEach
    void init(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
        this.formItemDTO = new FormItemDTO();
    }


    @Test
    public void givenFormItemOfTypeTextEntry_WithInvalidAttributes_ReturnsError() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setFormItemType(FormItemEntryType.TEXT_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 1);
        Assertions.assertTrue(messages.contains(TEXT_ENTRY_VALIDATION_MESSAGE));
    }

    @Test
    public void givenFormItemOfTypeTextEntry_WithValidAttributes_ReturnsNothing() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setFormItemType(FormItemEntryType.TEXT_ENTRY.name());
        formItemDTO.setPlaceholderText("Test Placeholder");
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 0);
        Assertions.assertFalse(messages.contains(TEXT_ENTRY_VALIDATION_MESSAGE));
    }

    @Test
    public void givenFormItemOfTypeMultipleEntry_WithInvalidAttributes_ReturnsError() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setFormItemType(FormItemEntryType.MULTIPLE_CHOICE_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 1);
        Assertions.assertTrue(messages.contains(MULTIPLE_CHOICE_ENTRY_VALIDATION_MESSAGE));
    }

    @Test
    public void givenFormItemOfTypeMultipleEntry_WithValidAttributes_ReturnsNothing() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setMaxLength(1);
        formItemDTO.setMultipleAllowed(true);
        formItemDTO.setOptions(List.of(new String[]{"option1", "option2"}));
        formItemDTO.setFormItemType(FormItemEntryType.MULTIPLE_CHOICE_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 0);
        Assertions.assertFalse(messages.contains(MULTIPLE_CHOICE_ENTRY_VALIDATION_MESSAGE));
    }


    @Test
    public void givenFormItemOfTypeSliderEntry_WithInvalidAttributes_ReturnsError() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setFormItemType(FormItemEntryType.SLIDER_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 1);
        Assertions.assertTrue(messages.contains(SLIDER_ENTRY_VALIDATION_MESSAGE));
    }

    @Test
    public void givenFormItemOfTypeSliderEntry_WithValidAttributes_ReturnsNothing() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setMinValue(1);
        formItemDTO.setMaxValue(10);
        formItemDTO.setFormItemType(FormItemEntryType.SLIDER_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 0);
        Assertions.assertFalse(messages.contains(SLIDER_ENTRY_VALIDATION_MESSAGE));
    }


    @Test
    public void givenFormItemOfTypeAcknowledgementEntry_WithInvalidAttributes_ReturnsError() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setFormItemType(FormItemEntryType.ACKNOWLEDGEMENT_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 1);
        Assertions.assertTrue(messages.contains(ACKNOWLEDGEMENT_ENTRY_VALIDATION_MESSAGE));
    }

    @Test
    public void givenFormItemOfTypeAcknowledgementEntry_WithValidAttributes_ReturnsNothing() {
        formItemDTO = new FormItemDTO();
        formItemDTO.setFormId((long)1223);
        formItemDTO.setTitle("Random Title");
        formItemDTO.setInformationText("Random Info");
        formItemDTO.setRequiredAcknowledgement(false);
        formItemDTO.setFormItemType(FormItemEntryType.ACKNOWLEDGEMENT_ENTRY.name());
        Set<String> messages = messages(validator.validate(formItemDTO));
        Assertions.assertEquals(messages.size(), 0);
        Assertions.assertFalse(messages.contains(ACKNOWLEDGEMENT_ENTRY_VALIDATION_MESSAGE));
    }

    private Set<String> messages(Set<ConstraintViolation<Object>> constraintViolations) {
        return constraintViolations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
