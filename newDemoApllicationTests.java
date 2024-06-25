package com.example.newDemo;

import static org.hamcrest.Matchers.greaterThan;

import com.example.newDemo.entity.Student;
import com.example.newDemo.repository.StudentRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class NewDemoApplicationTests {

	@Container
	@ServiceConnection
	private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("artifactory.global.standardchartered.com/postgres:15-alpine")
					.asCompatibleSubstituteFor("postgres")
	);
//	private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("artifactory.global.standardchartered.com/postgres:15-alpine");

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private MockMvc mockMvc;


	// Test for no of rows in db
	// given/when/then format
	@Test
	public void givenStudents_whenGetAllStudents_thenListOfStudents() throws Exception {

		System.out.println(postgreSQLContainer.getDatabaseName());
		System.out.println(postgreSQLContainer.getPassword());
		System.out.println(postgreSQLContainer.getUsername());
		System.out.println(postgreSQLContainer.getJdbcUrl());

		// given - setup or precondition
//		List<Student> students =
//				List.of(
//						new Student(1L, "Jon", "Snow", 100),
//						new Student(2L, "Jamie", "Lannister", 200)
//				);
//		studentRepository.saveAll(students);

		// when - action
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/students"));

		// then - verify the output
		response.andExpect(status().isOk());
		response.andExpect(MockMvcResultMatchers.jsonPath("$.length()", CoreMatchers.is(4)));
	}


    // test for adding a new student
	@Test
	public void givenStudent_whenAddStudent_thenStudentIsAdded() throws Exception{
		// Given
		Student newStudent = new Student(10L, "Arya", "Stark", 300);
		String studentJson = new ObjectMapper().writeValueAsString(newStudent);

		// When
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
				.contentType(MediaType.APPLICATION_JSON)
				.content(studentJson));

		// Then
		response.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("Arya")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Stark")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.stipend", CoreMatchers.is(300)));

		// Verify in the repository
		List<Student> students = studentRepository.findAll();
		assertThat(students).hasSize(1);
		assertThat(students.get(0).getFirstName()).isEqualTo("Arya");




	}

	// Test for updating an existing student
	@Test
	public void givenUpdatedStudent_whenUpdateStudent_thenStudentIsUpdated() throws Exception {
		// Given
		Student student = new Student(null, "Sansa", "Stark", 400);
		studentRepository.save(student);
		Long studentId = student.getId();

		Student updatedStudent = new Student(studentId, "Sansa", "Lannister", 500);
		String updatedStudentJson = new ObjectMapper().writeValueAsString(updatedStudent);

		// When
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/students/" + studentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedStudentJson));

		// Then
		response.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Lannister")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.stipend", CoreMatchers.is(500)));

		// Verify in the repository
		Student savedStudent = studentRepository.findById(studentId).orElse(null);
		System.out.print("Saved Student: " + savedStudent);
		Student updatedStudentFromDb = studentRepository.findById(studentId).get();
		assertThat(updatedStudentFromDb.getLastName()).isEqualTo("Lannister");
		assertThat(updatedStudentFromDb.getStipend()).isEqualTo(500);
	}

	// Test for Deleting a student

	@Test
	public void givenStudentId_whenDeleteStudent_thenStudentIsDeleted() throws Exception {
		// Given
		Student student = new Student(null, "Bran", "Stark", 600);
		studentRepository.save(student);
		Long studentId = student.getId();

		// When
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/students/" + studentId));

		// Then
		response.andExpect(status().isNoContent());

		// Verify in the repository
		boolean studentExists = studentRepository.existsById(studentId);
		assertThat(studentExists).isFalse();
	}

	// Test for Getting a Single Student by ID
	@Test
	public void givenStudentId_whenGetStudentById_thenReturnStudent() throws Exception {
		// Given
		Student student = new Student(null, "Robb", "Stark", 700);
		studentRepository.save(student);
		Long studentId = student.getId();

		// When
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/students/" + studentId));

		// Then
		response.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("Robb")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Stark")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.stipend", CoreMatchers.is(700)));
	}


	// Test for Handling Non-Existent Student
	@Test
	public void givenNonExistentStudentId_whenGetStudentById_thenReturnNotFound() throws Exception {
		// Given
		Long nonExistentId = 999L;

		// When
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/students/" + nonExistentId));

		// Then
		response.andExpect(status().isNotFound());
	}

	// Test for stipend above 20000

	@Test
	public void givenStudent_whenStipendAbove20000_thenReturnTrue() throws
			Exception{
		// Given
		Student student = new Student(null, "Jon", "Snow", 25000);
		studentRepository.save(student);

		// When
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/students/" + student.getId()));

		// Then
		response.andExpect(status().isOk())
				.andExpect(jsonPath("$.stipend", greaterThan(20000)));
	}

}
