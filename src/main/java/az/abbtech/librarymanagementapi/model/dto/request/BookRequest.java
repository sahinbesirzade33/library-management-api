
package az.abbtech.librarymanagementapi.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(

        @NotBlank(message = "{book.title.notblank}")
        String title,

        @NotBlank(message = "{book.author.notblank}")
        String author,

        @NotBlank(message = "{book.isbn.notblank}")
        String isbn,

        @NotNull(message = "{book.total-copies.notnull}")
        @Min(value = 1, message = "{book.total-copies.min}")
        Integer totalCopies

) {
}
