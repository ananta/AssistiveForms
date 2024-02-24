package com.anntz.formservice.controller;

import com.anntz.formservice.dto.CreateFormDTO;
import com.anntz.formservice.dto.FormResponseDTO;
import com.anntz.formservice.model.Form;
import com.anntz.formservice.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/form")
@RequiredArgsConstructor
public class FormController {
    private final FormService formService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Form createForm(@RequestBody CreateFormDTO createFormDto){
        return formService.createForm(createFormDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FormResponseDTO> getAllForms(){
        return formService.getAllForms();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FormResponseDTO getForm(@PathVariable Long id){
        return formService.getForm(id);
    }

}
