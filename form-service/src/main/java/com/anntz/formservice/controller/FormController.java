package com.anntz.formservice.controller;

import com.anntz.formservice.dto.CreateFormDTO;
import com.anntz.formservice.dto.FormResponseDTO;
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
    public void createForm(@RequestBody CreateFormDTO createFormDto){
        formService.createForm(createFormDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FormResponseDTO> getAllForms(){
        return formService.getAllForms();
    }

}
