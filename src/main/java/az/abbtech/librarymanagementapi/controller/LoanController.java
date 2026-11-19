
package az.abbtech.librarymanagementapi.controller;

import az.abbtech.librarymanagementapi.model.dto.request.LoanRequest;
import az.abbtech.librarymanagementapi.model.dto.response.LoanResponse;
import az.abbtech.librarymanagementapi.model.entity.Loan;
import az.abbtech.librarymanagementapi.mapper.LoanMapper;
import az.abbtech.librarymanagementapi.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrowBook(
            @Valid @RequestBody LoanRequest request
    ) {

        Loan loan = loanService.borrowBook(
                request.bookId(),
                request.memberId()
        );

        return LoanMapper.toResponse(loan);
    }

    @PutMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {

        Loan loan = loanService.returnBook(id);

        return LoanMapper.toResponse(loan);
    }

    @GetMapping
    public List<LoanResponse> getAllLoans() {

        return loanService.getAllLoans()
                .stream()
                .map(LoanMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public LoanResponse getLoanById(@PathVariable Long id) {

        Loan loan = loanService.getLoanById(id);

        return LoanMapper.toResponse(loan);
    }
}
