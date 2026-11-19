
package az.abbtech.librarymanagementapi.controller;

import az.abbtech.librarymanagementapi.model.entity.Book;
import az.abbtech.librarymanagementapi.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void createBook_shouldReturnCreated() throws Exception {
        Book book = createBook();

        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Clean Code",
                                    "author": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "totalCopies": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.totalCopies").value(5))
                .andExpect(jsonPath("$.availableCopies").value(5));

        verify(bookService).createBook(any(Book.class));
    }

    @Test
    void getAllBooks_shouldReturnBookList() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(createBook()));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));

        verify(bookService).getAllBooks();
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(createBook());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.isbn").value("9780132350884"));

        verify(bookService).getBookById(1L);
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        Book book = createBook();
        book.setTitle("Effective Java");

        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Effective Java",
                                    "author": "Joshua Bloch",
                                    "isbn": "9780134685991",
                                    "totalCopies": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java"));

        verify(bookService).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }

    @Test
    void createBook_shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "author": "Robert C. Martin",
                                    "isbn": "9780132350884",
                                    "totalCopies": 5
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).createBook(any(Book.class));
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780132350884");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        return book;
    }
}
