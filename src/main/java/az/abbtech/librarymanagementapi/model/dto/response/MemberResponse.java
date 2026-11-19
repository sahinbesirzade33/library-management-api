
package az.abbtech.librarymanagementapi.model.dto.response;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
