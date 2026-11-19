package az.abbtech.librarymanagementapi.service;

import az.abbtech.librarymanagementapi.model.entity.Loan;

import java.util.List;

public interface LoanService {

    Loan borrowBook(Long bookId, Long memberId);

    Loan returnBook(Long loanId);

    List<Loan> getAllLoans();

    Loan getLoanById(Long id);
}