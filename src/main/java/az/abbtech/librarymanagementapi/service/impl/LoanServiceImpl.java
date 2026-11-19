
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
import az.abbtech.librarymanagementapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final MessageSource messageSource;

    @Override
    public Loan borrowBook(Long bookId, Long memberId) {

        Book book = bookRepository.findByIdWithLock(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "book.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "book.unavailable",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        Member member = memberRepository.findByIdWithLock(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "member.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        long activeLoans = loanRepository.countByMemberIdAndStatus(
                memberId,
                LoanStatus.ACTIVE
        );

        if (activeLoans >= 3) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "loan.limit-reached",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowDate(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);

        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Override
    public Loan returnBook(Long loanId) {

        Loan loan = loanRepository.findByIdWithLock(loanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "loan.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "loan.already-returned",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        Book book = bookRepository.findByIdWithLock(
                loan.getBook().getId()
        ).orElseThrow(() -> new ResourceNotFoundException(
                messageSource.getMessage(
                        "book.not-found",
                        null,
                        LocaleContextHolder.getLocale()
                )
        ));

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "book.available-copies.invalid",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "loan.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));
    }
}
