
package az.abbtech.librarymanagementapi.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanRequest(

        @NotNull(message = "{loan.book-id.notnull}")
        @Positive(message = "{loan.book-id.positive}")
        Long bookId,

        @NotNull(message = "{loan.member-id.notnull}")
        @Positive(message = "{loan.member-id.positive}")
        Long memberId

) {
}
