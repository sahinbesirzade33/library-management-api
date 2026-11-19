
package az.abbtech.librarymanagementapi.service.impl;

import az.abbtech.librarymanagementapi.exception.BusinessException;
import az.abbtech.librarymanagementapi.exception.ResourceNotFoundException;
import az.abbtech.librarymanagementapi.model.entity.Book;
import az.abbtech.librarymanagementapi.repository.BookRepository;
import az.abbtech.librarymanagementapi.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_shouldCreateBookSuccessfully() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780132350884");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.createBook(book);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals(5, result.getTotalCopies());
        assertEquals(5, result.getAvailableCopies());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void getBookById_shouldReturnBookWhenBookExists() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void getBookById_shouldThrowExceptionWhenBookDoesNotExist() {
        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getBookById(1L)
        );
    }

    @Test
    void deleteBook_shouldDeleteBookWhenNoLoanHistoryExists() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(book));

        when(loanRepository.existsByBook_Id(1L))
                .thenReturn(false);

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_shouldThrowExceptionWhenLoanHistoryExists() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(book));

        when(loanRepository.existsByBook_Id(1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> bookService.deleteBook(1L)
        );

        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void deleteBook_shouldThrowExceptionWhenBookDoesNotExist() {
        when(bookRepository.findByIdWithLock(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.deleteBook(1L)
        );

        verify(bookRepository, never()).delete(any(Book.class));
    }
}
