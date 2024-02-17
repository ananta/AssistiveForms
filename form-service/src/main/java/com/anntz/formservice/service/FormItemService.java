package com.anntz.formservice.service;

import com.anntz.formservice.dto.FormItemDTO;
import com.anntz.formservice.exception.FormNotFoundException;
import com.anntz.formservice.model.*;
import com.anntz.formservice.repository.FormItemRepository;
import com.anntz.formservice.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormItemService {
    private final FormRepository  formRepository;
    private final FormItemRepository formItemRepository;

    public FormItem createFormItem(long formId, FormItemDTO formItemDTO){
        // TODO: Check if the user has rights to create forms on the given formItem
        Form form = getFormById(formId);
        if (form == null) {
            throw new FormNotFoundException("Form not found");
        }

        // FIXME: (refactor) can be optimized
        FormItem formItem = switch (formItemDTO.getFormItemType()) {
            case "TEXT_ENTRY" -> TextEntryItem.builder()
                    .placeholderText(formItemDTO.getPlaceholderText())
                    .build();
            case "MULTIPLE_CHOICE_ENTRY" -> MultipleChoiceEntryItem.builder()
                    .options(formItemDTO.getOptions())
                    .multipleAllowed(formItemDTO.getMultipleAllowed())
                    .build();
            case "SLIDER_ENTRY" -> SliderEntryItem.builder()
                    .maxValue(formItemDTO.getMaxValue())
                    .minValue(formItemDTO.getMinValue())
                    .build();
            case "ACKNOWLEDGEMENT_ENTRY" -> AcknowledgementEntryItem.builder()
                    .informationText(formItemDTO.getInformationText())
                    .requiredAcknowledgement(formItemDTO.getRequiredAcknowledgement())
                    .build();
            // Fallback to basic form item
            default -> InformationEntryItem.builder().build();
        };

        formItem.setPreviousFormItemId(formItemDTO.getPreviousFormItemId());
        formItem.setFormItemType(formItemDTO.getFormItemType());
        formItem.setNextFormItemId(formItemDTO.getNextFormItemId());
        formItem.setTitle(formItemDTO.getTitle());
        formItem.setDescription(formItemDTO.getDescription());
        formItem.setForm(form);

        FormItem newFormItem = formItemRepository.save(formItem);
        log.info("FormItem {} created!", formItem.getId());
        return newFormItem;
    }

    public void updateFormItems(){

    }

    public FormItemDTO getFormItem(long formId, long formItemId) {
        Optional<FormItem> formItem = formItemRepository.findById(formItemId);
        if (formItem.isEmpty() || !Objects.equals(formItem.get().getForm().getId(), formId)){
            throw new FormNotFoundException("Form item not found");
        }
        return this.mapToFormItemDTO(formItem.get());
    }

    private Form getFormById(long formId){
        return formRepository.findById(formId).orElseThrow(() -> new FormNotFoundException(String.format("Form with id: %s not found", formId)));
    }
    public List<FormItemDTO> getFormItems(long formId){
        getFormById(formId);
        List<FormItem> formItems = formItemRepository.findByFormId(formId);
        return formItems.stream().map(this::mapToFormItemDTO).toList();
    }

    public void removeFormItem(long formId){
        // TODO: Check if user is the owner of the formItem
        formItemRepository.deleteByFormId(formId);
    }

    private FormItemDTO mapToFormItemDTO(FormItem formItem){
        FormItemDTO.FormItemDTOBuilder formItemDTOBuilder = FormItemDTO.builder()
                .id(formItem.getId())
                .formId(formItem.getForm().getId())
                .previousFormItemId(formItem.getPreviousFormItemId())
                .nextFormItemId(formItem.getNextFormItemId())
                .title(formItem.getTitle())
                .description(formItem.getDescription())
                .formItemType(formItem.getFormItemType());

        if (formItem instanceof TextEntryItem textEntryItem){
            formItemDTOBuilder.placeholderText(textEntryItem.getPlaceholderText());
        }else if(formItem instanceof MultipleChoiceEntryItem multipleChoiceEntryItem) {
            formItemDTOBuilder.options(multipleChoiceEntryItem.getOptions());
            formItemDTOBuilder.multipleAllowed(multipleChoiceEntryItem.getMultipleAllowed());
        }else if(formItem instanceof SliderEntryItem sliderEntryItem) {
            formItemDTOBuilder.maxValue(sliderEntryItem.getMaxValue());
            formItemDTOBuilder.minValue(sliderEntryItem.getMinValue());
        }else if(formItem instanceof AcknowledgementEntryItem acknowledgementEntryItem) {
            formItemDTOBuilder.informationText(acknowledgementEntryItem.getInformationText());
            formItemDTOBuilder.requiredAcknowledgement(acknowledgementEntryItem.getRequiredAcknowledgement());
        }

        return formItemDTOBuilder.build();
    }

}
