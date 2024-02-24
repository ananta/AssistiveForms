package com.anntz.formservice.controller;

import com.anntz.formservice.dto.FormItemDTO;
import com.anntz.formservice.model.FormItem;
import com.anntz.formservice.service.FormItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/form/{formId}/formItems")
@RequiredArgsConstructor
public class FormItemController {
    private final FormItemService formItemService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<FormItemDTO> getFormItems(@PathVariable("formId") Long formId) {
        return formItemService.getFormItems(formId);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FormItemDTO getFormItem(@PathVariable("formId") Long formItemId, @PathVariable Long id) {
        return formItemService.getFormItem(formItemId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormItem createFormItem(@PathVariable("formId") Long formId, @Valid @RequestBody FormItemDTO formItemDto) {
        return formItemService.createFormItem(formId, formItemDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateFormItems(@PathVariable("formId") Long formId, @Valid @RequestBody FormItemDTO[] formItemDTOS) {
        // TODO: Add validation to check for linked formItems
        // Maybe create a custom spring validator that checks if upcoming sequence is valid
        formItemService.updateFormItems(formId, formItemDTOS);
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void removeFormItem(@PathVariable("formId") Long formId) {
        formItemService.removeFormItem(formId);
    }
}
