
package az.abbtech.librarymanagementapi.mapper;

import az.abbtech.librarymanagementapi.model.dto.request.BookRequest;
import az.abbtech.librarymanagementapi.model.dto.response.BookResponse;
import az.abbtech.librarymanagementapi.model.entity.Book;

public class BookMapper {

    public static Book toEntity(BookRequest request) {

        Book book = new Book();

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies());

        return book;
    }

    public static BookResponse toResponse(Book book) {

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
