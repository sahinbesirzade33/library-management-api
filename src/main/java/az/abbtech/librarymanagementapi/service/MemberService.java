package az.abbtech.librarymanagementapi.service;

import az.abbtech.librarymanagementapi.model.entity.Member;

import java.util.List;

public interface MemberService {

    Member createMember(Member member);

    List<Member> getAllMembers();

    Member getMemberById(Long id);

    Member updateMember(Long id, Member member);

    void deleteMember(Long id);
}