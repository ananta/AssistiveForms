package com.anntz.formservice.service;

import com.anntz.formservice.dto.CreateFormDTO;
import com.anntz.formservice.dto.FormResponseDTO;
import com.anntz.formservice.exception.FormNotFoundException;
import com.anntz.formservice.model.Form;
import com.anntz.formservice.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormService {
    private final FormRepository formRepository;

    public Form createForm(CreateFormDTO createFormDto){
        Form form  = Form.builder().name(createFormDto.getName())
                .description(createFormDto.getDescription())
                .startDate(createFormDto.getStartDate())
                .endDate(createFormDto.getEndDate())
                .build();
        Form newForm = formRepository.save(form);
        log.info("From {} created!", form.getId());
        return newForm;
    }

    public List<FormResponseDTO> getAllForms(){
        List<Form> forms = formRepository.findAll();
        return forms.stream().map(this::mapToFormResponse).toList();
    }

    public FormResponseDTO getForm(Long id){
        Form form = formRepository.findById(id).orElseThrow(() -> new FormNotFoundException(String.format("Form with id: %s not found", id)));
        return mapToFormResponse(form);
    }

    private FormResponseDTO mapToFormResponse(Form form) {
        return FormResponseDTO.builder()
                .id(form.getId().toString())
                .name(form.getName())
                .description(form.getDescription())
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .build();
    }
}
