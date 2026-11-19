
package az.abbtech.librarymanagementapi.controller;

import az.abbtech.librarymanagementapi.model.entity.Book;
import az.abbtech.librarymanagementapi.model.entity.Loan;
import az.abbtech.librarymanagementapi.model.entity.Member;
import az.abbtech.librarymanagementapi.model.enums.LoanStatus;
import az.abbtech.librarymanagementapi.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Test
    void borrowBook_shouldReturnCreated() throws Exception {
        when(loanService.borrowBook(1L, 1L)).thenReturn(createLoan());

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "bookId": 1,
                                    "memberId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(loanService).borrowBook(1L, 1L);
    }

    @Test
    void borrowBook_shouldReturnBadRequestWhenBookIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "memberId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).borrowBook(any(), any());
    }

    @Test
    void returnBook_shouldReturnUpdatedLoan() throws Exception {
        Loan loan = createLoan();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        when(loanService.returnBook(1L)).thenReturn(loan);

        mockMvc.perform(put("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.memberId").value(1));

        verify(loanService).returnBook(1L);
    }

    @Test
    void getAllLoans_shouldReturnLoanList() throws Exception {
        when(loanService.getAllLoans()).thenReturn(List.of(createLoan()));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(loanService).getAllLoans();
    }

    @Test
    void getLoanById_shouldReturnLoan() throws Exception {
        when(loanService.getLoanById(1L)).thenReturn(createLoan());

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(loanService).getLoanById(1L);
    }

    private Loan createLoan() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");

        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Ali");
        member.setLastName("Mammadov");

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);

        return loan;
    }
}
