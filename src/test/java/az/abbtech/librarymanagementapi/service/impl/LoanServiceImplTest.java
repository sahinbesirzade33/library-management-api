
package az.abbtech.librarymanagementapi.service.impl;

import az.abbtech.librarymanagementapi.exception.BusinessException;
import az.abbtech.librarymanagementapi.exception.ResourceNotFoundException;
import az.abbtech.librarymanagementapi.model.entity.Book;
import az.abbtech.librarymanagementapi.model.entity.Loan;
import az.abbtech.librarymanagementapi.model.entity.Member;
import az.abbtech.librarymanagementapi.model.enums.LoanStatus;
import az.abbtech.librarymanagementapi.repository.BookRepository;
import az.abbtech.librarymanagementapi.repository.LoanRepository;
import az.abbtech.librarymanagementapi.repository.MemberRepository;
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
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void borrowBook_shouldBorrowBookSuccessfully() {
        Book book = new Book();
        book.setId(1L);
        book.setTotalCopies(5);
        book.setAvailableCopies(2);

        Member member = new Member();
        member.setId(1L);

        when(bookRepository.findByIdWithLock(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findByIdWithLock(1L)).thenReturn(Optional.of(member));
        when(loanRepository.countByMemberIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(1L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.borrowBook(1L, 1L);

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        assertEquals(1, book.getAvailableCopies());

        verify(bookRepository).save(book);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenBookDoesNotExist() {
        when(bookRepository.findByIdWithLock(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(999L, 1L));

        verifyNoInteractions(memberRepository);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenBookIsUnavailable() {
        Book book = new Book();
        book.setId(1L);
        book.setTotalCopies(5);
        book.setAvailableCopies(0);

        when(bookRepository.findByIdWithLock(1L)).thenReturn(Optional.of(book));

        assertThrows(BusinessException.class, () -> loanService.borrowBook(1L, 1L));

        verifyNoInteractions(memberRepository);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenMemberHasThreeActiveLoans() {
        Book book = new Book();
        book.setId(1L);
        book.setTotalCopies(5);
        book.setAvailableCopies(2);

        Member member = new Member();
        member.setId(1L);

        when(bookRepository.findByIdWithLock(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findByIdWithLock(1L)).thenReturn(Optional.of(member));
        when(loanRepository.countByMemberIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(3L);

        assertThrows(BusinessException.class, () -> loanService.borrowBook(1L, 1L));

        assertEquals(2, book.getAvailableCopies());
        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenMemberDoesNotExist() {
        Book book = new Book();
        book.setId(1L);
        book.setTotalCopies(5);
        book.setAvailableCopies(2);

        when(bookRepository.findByIdWithLock(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findByIdWithLock(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(1L, 999L));

        assertEquals(2, book.getAvailableCopies());
        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void returnBook_shouldReturnBookSuccessfully() {
        Book book = new Book();
        book.setId(1L);
        book.setTotalCopies(5);
        book.setAvailableCopies(2);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findByIdWithLock(1L)).thenReturn(Optional.of(loan));
        when(bookRepository.findByIdWithLock(1L)).thenReturn(Optional.of(book));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.returnBook(1L);

        assertNotNull(result);
        assertEquals(LoanStatus.RETURNED, result.getStatus());
        assertNotNull(result.getReturnDate());
        assertEquals(3, book.getAvailableCopies());

        verify(bookRepository).save(book);
        verify(loanRepository).save(loan);
    }

    @Test
    void returnBook_shouldThrowExceptionWhenLoanAlreadyReturned() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findByIdWithLock(1L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class, () -> loanService.returnBook(1L));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_shouldThrowExceptionWhenLoanDoesNotExist() {
        when(loanRepository.findByIdWithLock(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.returnBook(999L));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_shouldThrowExceptionWhenAvailableCopiesReachTotalCopies() {
        Book book = new Book();
        book.setId(1L);
        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findByIdWithLock(1L)).thenReturn(Optional.of(loan));
        when(bookRepository.findByIdWithLock(1L)).thenReturn(Optional.of(book));

        assertThrows(BusinessException.class, () -> loanService.returnBook(1L));

        assertEquals(5, book.getAvailableCopies());
        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }
}
