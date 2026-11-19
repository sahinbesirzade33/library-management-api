
package az.abbtech.librarymanagementapi.mapper;

import az.abbtech.librarymanagementapi.model.dto.response.LoanResponse;
import az.abbtech.librarymanagementapi.model.entity.Loan;

public class LoanMapper {

    public static LoanResponse toResponse(Loan loan) {

        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getMember().getId(),
                loan.getBorrowDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
}
