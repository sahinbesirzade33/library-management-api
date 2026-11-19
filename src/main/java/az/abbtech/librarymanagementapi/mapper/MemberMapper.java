
package az.abbtech.librarymanagementapi.mapper;

import az.abbtech.librarymanagementapi.model.dto.request.MemberRequest;
import az.abbtech.librarymanagementapi.model.dto.response.MemberResponse;
import az.abbtech.librarymanagementapi.model.entity.Member;

public class MemberMapper {

    public static Member toEntity(MemberRequest request) {

        Member member = new Member();

        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());

        return member;
    }

    public static MemberResponse toResponse(Member member) {

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail()
        );
    }
}
