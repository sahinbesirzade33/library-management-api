package az.abbtech.librarymanagementapi.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Min(0)
    @Column(nullable = false)
    private Integer totalCopies;

    @Min(0)
    @Column(nullable = false)
    private Integer availableCopies;
}