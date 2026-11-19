
package az.abbtech.librarymanagementapi.service.impl;

import az.abbtech.librarymanagementapi.exception.BusinessException;
import az.abbtech.librarymanagementapi.exception.ResourceNotFoundException;
import az.abbtech.librarymanagementapi.model.entity.Book;
import az.abbtech.librarymanagementapi.repository.BookRepository;
import az.abbtech.librarymanagementapi.repository.LoanRepository;
import az.abbtech.librarymanagementapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final MessageSource messageSource;

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "book.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));
    }

    @Override
    public Book updateBook(Long id, Book updatedBook) {

        Book book = bookRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "book.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

        if (updatedBook.getTotalCopies() < borrowedCopies) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "book.total-copies.invalid",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setIsbn(updatedBook.getIsbn());
        book.setTotalCopies(updatedBook.getTotalCopies());
        book.setAvailableCopies(updatedBook.getTotalCopies() - borrowedCopies);

        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {

        Book book = bookRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "book.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        if (loanRepository.existsByBook_Id(id)) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "book.has-loan-history",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        bookRepository.delete(book);
    }
}
