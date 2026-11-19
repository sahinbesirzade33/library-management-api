
package az.abbtech.librarymanagementapi.model.dto.response;

import az.abbtech.librarymanagementapi.model.enums.LoanStatus;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long bookId,
        Long memberId,
        LocalDate borrowDate,
        LocalDate returnDate,
        LoanStatus status
) {
}
