package com.anntz.formservice;

import com.anntz.formservice.dto.CreateFormDTO;
import com.anntz.formservice.dto.FormItemDTO;
import com.anntz.formservice.model.Form;
import com.anntz.formservice.repository.FormItemRepository;
import com.anntz.formservice.repository.FormRepository;
import com.anntz.formservice.utils.RandomTextGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class FormServiceApplicationTests {

    DateFormat dateFormat;

    public FormServiceApplicationTests() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.3");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FormItemRepository formItemRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void createForm_WithValidFormContent_ReturnsSuccess() throws Exception {
        CreateFormDTO createFormDTO = getFormRequest();
        String createFormRequestContent = objectMapper.writeValueAsString(createFormDTO);
        long formsAtTheMoment = formRepository.findAll().stream().count();
        // mock a servlet application(MOCK MVC) and make the endpoint calls
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormRequestContent))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Assertions.assertEquals(formsAtTheMoment + 1, formRepository.findAll().size());
        formRepository.deleteAll();
    }

    @Test
    void createForm_WithInValidFormContent_ReturnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getForms_WithNoParams_ReturnsListOfForms() throws Exception {
        int NUMBER_OF_FORMS_TO_CREATE = 10;
        for (int i = 0; i < NUMBER_OF_FORMS_TO_CREATE; i++) {
            Form form = Form.builder()
                    .name("FormName")
                    .description("FormDescription")
                    .startDate(new Date())
                    .endDate(new Date())
                    .build();
            formRepository.save(form);
        }

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(NUMBER_OF_FORMS_TO_CREATE));
        formRepository.deleteAll();
    }

    @Test
    void getForm_WithWrongFormId_ReturnsNotFound() throws Exception {
        long FORM_ID = 99999999;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/form/", FORM_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getForm_WithInvalidFormId_ReturnsNotFound() throws Exception {
        var FORM_ID = "1000InvalidFormId";
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/form/" + FORM_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getForm_WithValidId_ReturnsForm() throws Exception {
        String FORM_NAME = "FormName";
        String FORM_DESCRIPTION = "FromDescription";

        // TODO: add tests for: startDate < endDate
        Date FORM_DATE = new Date();
        Form form = Form.builder()
                .name(FORM_NAME)
                .description(FORM_DESCRIPTION)
                .startDate(FORM_DATE)
                .endDate(FORM_DATE)
                .build();
        Form savedForm = formRepository.save(form);

        String expectedDate = this.dateFormat.format(FORM_DATE).replace("Z", "+00:00");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/form/" + savedForm.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedForm.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(FORM_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(FORM_DESCRIPTION))
                .andExpect(MockMvcResultMatchers.jsonPath("$.startDate").value(expectedDate))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endDate").value(expectedDate));
        formRepository.deleteAll();
    }


    @Test
    void createFormItem_WithInvalidFormItemContent_ReturnsSuccess() throws Exception {
        Form form = formRepository.save(
                Form.builder()
                        .name(RandomTextGenerator.generateRandomText(10))
                        .description(RandomTextGenerator.generateRandomText(30))
                        .startDate(new Date())
                        .endDate(new Date())
                        .build()
        );
        FormItemDTO formItemDTO = FormItemDTO.builder().id(1L).title("klasjdf").build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + RandomTextGenerator.generateRandomText(2) + "/formItems"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        formItemDTO.setPreviousFormItemId(null);
        formItemDTO.setNextFormItemId(null);
        formItemDTO.setDescription(RandomTextGenerator.generateRandomText((20)));
        formItemDTO.setFormItemType("MULTIPLE_CHOICE_ENTRY");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        formRepository.deleteAll();
    }

        @Test
        void createFormItem_WithValidFormItemContent_ReturnsSuccess () throws Exception {
            Form form = formRepository.save(
                    Form.builder()
                            .name(RandomTextGenerator.generateRandomText(10))
                            .description(RandomTextGenerator.generateRandomText(30))
                            .startDate(new Date())
                            .endDate(new Date())
                            .build()
            );
            FormItemDTO formItemDTO = FormItemDTO.builder().id(1L).build();

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/form/" + form.getId() + "/formItems")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(String.valueOf(formItemDTO)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
            formItemDTO.setTitle(RandomTextGenerator.generateRandomText(10));
            formItemDTO.setDescription(RandomTextGenerator.generateRandomText((20)));
            formItemDTO.setPreviousFormItemId(null);
            formItemDTO.setNextFormItemId(null);
            formItemDTO.setFormItemType("INFORMATION_ENTRY");
            formItemDTO.setPlaceholderText(RandomTextGenerator.generateRandomText(20));
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/form/" + form.getId() + "/formItems")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(formItemDTO)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

            formRepository.deleteAll();
            formItemRepository.deleteAll();
        }

    @Test
    void createTextEntryFormItem() throws Exception {
        Form form = formRepository.save(
                Form.builder()
                        .name(RandomTextGenerator.generateRandomText(10))
                        .description(RandomTextGenerator.generateRandomText(30))
                        .startDate(new Date())
                        .endDate(new Date())
                        .build()
        );
        FormItemDTO formItemDTO = FormItemDTO.builder().id(form.getId())
                .title(RandomTextGenerator.generateRandomText(10))
                .description(RandomTextGenerator.generateRandomText(20))
                .nextFormItemId(null)
                .previousFormItemId(null)
                .formItemType("TEXT_ENTRY")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        formItemDTO.setPlaceholderText(RandomTextGenerator.generateRandomText(10));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        formRepository.deleteAll();
        formItemRepository.deleteAll();
    }

    @Test
    void createMultipleChoiceEntryFormItem() throws Exception {
        Form form = formRepository.save(
                Form.builder()
                        .name(RandomTextGenerator.generateRandomText(10))
                        .description(RandomTextGenerator.generateRandomText(30))
                        .startDate(new Date())
                        .endDate(new Date())
                        .build()
        );
        FormItemDTO formItemDTO = FormItemDTO.builder().id(form.getId())
                .title(RandomTextGenerator.generateRandomText(10))
                .description(RandomTextGenerator.generateRandomText(20))
                .nextFormItemId(null)
                .previousFormItemId(null)
                .formItemType("MULTIPLE_CHOICE_ENTRY")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        formItemDTO.setMultipleAllowed(false);
        formItemDTO.setOptions(List.of(new String[]{"Apple", "Ball", "Cat"}));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        formItemRepository.deleteAll();
        formRepository.deleteAll();
    }

    @Test
    void createSliderEntryFormItem() throws Exception {
        Form form = formRepository.save(
                Form.builder()
                        .name(RandomTextGenerator.generateRandomText(10))
                        .description(RandomTextGenerator.generateRandomText(30))
                        .startDate(new Date())
                        .endDate(new Date())
                        .build()
        );
        FormItemDTO formItemDTO = FormItemDTO.builder().id(form.getId())
                .title(RandomTextGenerator.generateRandomText(10))
                .description(RandomTextGenerator.generateRandomText(20))
                .nextFormItemId(null)
                .previousFormItemId(null)
                .formItemType("SLIDER_ENTRY")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        formItemDTO.setMinValue(0);
        formItemDTO.setMaxValue(100);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        formItemRepository.deleteAll();
        formRepository.deleteAll();
    }

    @Test
    void createAcknowledgementEntryFormItem() throws Exception {
        Form form = formRepository.save(
                Form.builder()
                        .name(RandomTextGenerator.generateRandomText(10))
                        .description(RandomTextGenerator.generateRandomText(30))
                        .startDate(new Date())
                        .endDate(new Date())
                        .build()
        );
        FormItemDTO formItemDTO = FormItemDTO.builder().id(form.getId())
                .title(RandomTextGenerator.generateRandomText(10))
                .description(RandomTextGenerator.generateRandomText(20))
                .nextFormItemId(null)
                .previousFormItemId(null)
                .formItemType("ACKNOWLEDGEMENT_ENTRY")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        formItemDTO.setInformationText(RandomTextGenerator.generateRandomText(10));
        formItemDTO.setRequiredAcknowledgement(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/form/" + form.getId() + "/formItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(formItemDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        formItemRepository.deleteAll();
        formRepository.deleteAll();
    }


    private CreateFormDTO getFormRequest() {
        return CreateFormDTO.builder()
                .name("First Test Form")
                .description("Description of First Test Form")
                .startDate(convertLocalDateToDate(LocalDate.now()))
                .endDate(convertLocalDateToDate(LocalDate.now().plusDays(5)))
                .build();
    }

    private Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}