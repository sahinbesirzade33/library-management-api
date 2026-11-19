
package az.abbtech.librarymanagementapi.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRequest(

        @NotBlank(message = "{member.first-name.notblank}")
        String firstName,

        @NotBlank(message = "{member.last-name.notblank}")
        String lastName,

        @NotBlank(message = "{member.email.notblank}")
        @Email(message = "{member.email.invalid}")
        String email

) {
}
