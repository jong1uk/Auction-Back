package com.example.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @Column(nullable = false, length = 50)
    private String inquiryTitle;

    @Column(nullable = false)
    private LocalDate inquiryDate;

    @Column(nullable = true)
    private LocalDate modDate;

    @Column(nullable = false, length = 50)
    private String inquiryContent;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
