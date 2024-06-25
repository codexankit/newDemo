package com.example.newDemo.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Data
@Builder
//@AllArgsConstructor
@Entity
@Table(name = "studentsa")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;


    private int stipend;


    public Student() {
    }

    public Student(Long id, String firstName, String lastName, int stipend) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.stipend = stipend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getStipend() {
        return stipend;
    }

    public void setStipend(int stipend) {
        this.stipend = stipend;
    }
}
