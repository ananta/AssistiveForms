package com.anntz.formservice;

import com.anntz.formservice.dto.CreateFormDTO;
import com.anntz.formservice.model.Form;
import com.anntz.formservice.repository.FormRepository;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class FormServiceApplicationTests {

	DateFormat dateFormat;
	public void FormServiceApplication(){
		 // i know it's a disaster XD
		 this.dateFormat = new SimpleDateFormat("EE MMM d y H:m:s ZZZ");
	}

	@Container
	static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.3");

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FormRepository formRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Test
	void shouldCreateForm() throws Exception {
	 CreateFormDTO createFormDTO = getFormRequest();
	 String createFormRequestContent = objectMapper.writeValueAsString(createFormDTO);

		// mock a servlet application(MOCK MVC) and make the endpoint calls
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/form")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createFormRequestContent))
				.andExpect(MockMvcResultMatchers.status().isCreated());
        Assertions.assertEquals(1, formRepository.findAll().size());
		formRepository.deleteAll();
	}

	@Test
	void shouldListForms() throws Exception {
		CreateFormDTO createFormDTO = getFormRequest();
		String createFormRequest = objectMapper.writeValueAsString(createFormDTO);
		int NUMBER_OF_FORMS_TO_CREATE = 10;


		for (int i = 0; i< NUMBER_OF_FORMS_TO_CREATE; i++){
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

	private CreateFormDTO getFormRequest() {
		return CreateFormDTO.builder()
				.name("First Test Form")
				.description("Description of First Test Form")
				.startDate(convertLocalDateToDate(LocalDate.now()))
				.endDate(convertLocalDateToDate(LocalDate.now().plusDays(5)))
				.build();
	}

	private Date convertLocalDateToDate(LocalDate localDate){
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}


}
