package com.example.newDemo.controller;


import com.example.newDemo.entity.Student;
import com.example.newDemo.repository.StudentRepository;
import com.example.newDemo.entity.Student;
import com.example.newDemo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody Student student){
        return studentRepository.save(student);
    }

    @GetMapping
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id)
    {
        return studentRepository.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Student not found with id " + id));
    }
    
    @GetMapping("/firstname/{firstName}")
    public ResponseEntity<Student> getStudentByFirstName(@PathVariable String firstName)
    {
        Optional<Student> student = studentRepository.findByFirstName(firstName);
        if (student.isPresent())
        {
            return ResponseEntity.ok(student.get());
        }
        else
        {
            throw new ResourceNotFoundException("Student not found with first name " + firstName);
        }
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {

        System.out.println("Updating student with ID: " + id);
        System.out.println("Update URL: /api/students/" + id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("Student not found with id " + id));
        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setStipend(studentDetails.getStipend());

        return studentRepository.save(student);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }


}
