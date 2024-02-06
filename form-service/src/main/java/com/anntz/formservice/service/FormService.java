package com.anntz.formservice.service;

import com.anntz.formservice.dto.CreateFormDTO;
import com.anntz.formservice.dto.FormResponseDTO;
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

    public void createForm(CreateFormDTO createFormDto){
        Form form  = Form.builder().name(createFormDto.getName())
                .description(createFormDto.getDescription())
                .startDate(createFormDto.getStartDate())
                .endDate(createFormDto.getEndDate())
                .build();
        formRepository.save(form);
        log.info("From {} created!", form.getId());
    }

    public List<FormResponseDTO> getAllForms(){
        List<Form> forms = formRepository.findAll();
        return forms.stream().map(this::mapToFormResponse).toList();
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
