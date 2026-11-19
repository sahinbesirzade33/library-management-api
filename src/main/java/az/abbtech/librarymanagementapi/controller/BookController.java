
package az.abbtech.librarymanagementapi.controller;

import az.abbtech.librarymanagementapi.model.dto.request.BookRequest;
import az.abbtech.librarymanagementapi.model.dto.response.BookResponse;
import az.abbtech.librarymanagementapi.model.entity.Book;
import az.abbtech.librarymanagementapi.mapper.BookMapper;
import az.abbtech.librarymanagementapi.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody BookRequest request) {

        Book book = BookMapper.toEntity(request);

        Book savedBook = bookService.createBook(book);

        return BookMapper.toResponse(savedBook);
    }

    @GetMapping
    public List<BookResponse> getAllBooks() {

        return bookService.getAllBooks()
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {

        Book book = bookService.getBookById(id);

        return BookMapper.toResponse(book);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {

        Book book = BookMapper.toEntity(request);

        Book updatedBook = bookService.updateBook(id, book);

        return BookMapper.toResponse(updatedBook);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
    }
}
