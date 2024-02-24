package com.anntz.formservice.service;

import com.anntz.formservice.dto.FormItemDTO;
import com.anntz.formservice.exception.FormNotFoundException;
import com.anntz.formservice.model.*;
import com.anntz.formservice.repository.FormItemRepository;
import com.anntz.formservice.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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
        FormItem formItem = switch (formItemDTO.getFormItemType()){
            case "TEXT_ENTRY" -> new TextEntryItem();
            case "MULTIPLE_CHOICE_ENTRY" -> new MultipleChoiceEntryItem();
            case "SLIDER_ENTRY" -> new SliderEntryItem();
            case "ACKNOWLEDGEMENT_ENTRY" -> new AcknowledgementEntryItem();
            default -> new InformationEntryItem(); // fallback for basic form item
        };
        formItem.setNextItem(null);
        applyFormItemDTOToFormItem(formItem, formItemDTO);
        FormItem previousItem = this.getLastFormItem(formId);
        if (previousItem != null)
            formItem.setPreviousItem(previousItem.getId());
        else
            formItem.setPreviousItem(null);
        formItem.setForm(form);

        FormItem newFormItem = formItemRepository.save(formItem);
        if (previousItem != null){
            previousItem.setNextItem(newFormItem.getId());
            formItemRepository.save(previousItem);
        }
        log.info("FormItem {} created!", formItem.getId());
        return newFormItem;
    }

    public void updateFormItems(long formId, FormItemDTO[] formItemDTOS){
        Form form = this.formRepository.findById(formId).orElseThrow(() -> new FormNotFoundException(String.format("Form with id: %s not found", formId)));
        for (FormItemDTO formItem : formItemDTOS){
            if (!Objects.equals(formItem.getFormId(), form.getId()))
                throw new FormNotFoundException(String.format("Form with id: %s not found", formId));
            updateFormItem(formItem);
        }
    }

    public void updateFormItem(FormItemDTO formItemDTO) {
        FormItem formItem = this.formItemRepository.findById(formItemDTO.getId()).orElseThrow(() -> new FormNotFoundException(String.format("FormItem with id: %s not found", formItemDTO.getId())));
        if (formItemDTO.getTitle() != null) formItem.setTitle(formItemDTO.getTitle());
        if (formItemDTO.getDescription() != null) formItem.setDescription(formItemDTO.getDescription());
        applyFormItemDTOToFormItem(formItem, formItemDTO);
        formItem.setPreviousItem(formItemDTO.getPreviousFormItemId());
        formItem.setNextItem(formItemDTO.getNextFormItemId());
        formItemRepository.save(formItem);
        log.info("FormItem {} created!", formItem.getId());
    }

    public FormItemDTO getFormItem(long formId, long formItemId) {
        Optional<FormItem> formItem = formItemRepository.findById(formItemId);
        if (formItem.isEmpty() || !Objects.equals(formItem.get().getForm().getId(), formId)){
            throw new FormNotFoundException("Form item not found");
        }
        return this.mapToFormItemDTO(formItem.get());
    }

    public FormItem getLastFormItem(long formId) {
        return formItemRepository.findTopByFormIdAndNextItemIsNull(formId);
    }

    private Form getFormById(long formId){
        return formRepository.findById(formId).orElseThrow(() -> new FormNotFoundException(String.format("Form with id: %s not found", formId)));
    }
    public List<FormItemDTO> getFormItems(long formId){
        getFormById(formId);
        List<FormItem> formItems = formItemRepository.findByFormId(formId);
        List<FormItemDTO> formItemDTOS = new ArrayList<>(formItems.stream().map(this::mapToFormItemDTO).toList());
        formItemDTOS.sort((o1, o2) -> {
            if (o1.getPreviousFormItemId() == null && o2.getPreviousFormItemId() == null) return 0;
            if (o1.getPreviousFormItemId() == null) return -1;
            if (o2.getPreviousFormItemId() == null) return 1;
            return Long.compare(o1.getPreviousFormItemId(), o2.getPreviousFormItemId());
        });
        return formItemDTOS;
    }

    public void removeFormItem(long formId){
        // TODO: Check if user is the owner of the formItem
        formItemRepository.deleteByFormId(formId);
    }


    private void applyFormItemDTOToFormItem(FormItem formItem, FormItemDTO formItemDTO) {
        switch (formItemDTO.getFormItemType()) {
            case "TEXT_ENTRY" -> {
                if (formItem instanceof TextEntryItem && formItemDTO.getPlaceholderText() != null) ((TextEntryItem) formItem).setPlaceholderText(formItemDTO.getPlaceholderText());
            }

            case "MULTIPLE_CHOICE_ENTRY" -> {
                if (formItem instanceof MultipleChoiceEntryItem) {
                    if (formItemDTO.getOptions() != null) ((MultipleChoiceEntryItem) formItem).setOptions(formItemDTO.getOptions());
                    if (formItemDTO.getMultipleAllowed() != null) ((MultipleChoiceEntryItem) formItem).setMultipleAllowed(formItemDTO.getMultipleAllowed());
                }
            }

            case "SLIDER_ENTRY" -> {
                if (formItem instanceof SliderEntryItem) {
                    if (formItemDTO.getMaxValue() != null) ((SliderEntryItem) formItem).setMaxValue(formItemDTO.getMaxValue());
                    if (formItemDTO.getMinValue() != null) ((SliderEntryItem) formItem).setMinValue(formItemDTO.getMinValue());
                }
            }

            case "ACKNOWLEDGEMENT_ENTRY" -> {
                if (formItem instanceof AcknowledgementEntryItem && formItemDTO.getRequiredAcknowledgement() != null) {
                    ((AcknowledgementEntryItem) formItem).setRequiredAcknowledgement(formItemDTO.getRequiredAcknowledgement());
                    if (formItemDTO.getInformationText() != null) ((AcknowledgementEntryItem) formItem).setInformationText(formItemDTO.getInformationText());
                }
            }
        };

        formItem.setFormItemType(formItemDTO.getFormItemType());
        formItem.setTitle(formItemDTO.getTitle());
        formItem.setDescription(formItemDTO.getDescription());
    }

    private FormItemDTO mapToFormItemDTO(FormItem formItem){
        FormItemDTO.FormItemDTOBuilder formItemDTOBuilder = FormItemDTO.builder()
                .id(formItem.getId())
                .formId(formItem.getForm().getId())
                .previousFormItemId(formItem.getPreviousItem())
                .nextFormItemId(formItem.getNextItem())
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
