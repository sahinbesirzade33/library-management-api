
package az.abbtech.librarymanagementapi.service.impl;

import az.abbtech.librarymanagementapi.exception.BusinessException;
import az.abbtech.librarymanagementapi.exception.ResourceNotFoundException;
import az.abbtech.librarymanagementapi.model.entity.Member;
import az.abbtech.librarymanagementapi.repository.LoanRepository;
import az.abbtech.librarymanagementapi.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    void createMember_shouldCreateMemberSuccessfully() {
        Member member = new Member();
        member.setFirstName("Ali");
        member.setLastName("Mammadov");
        member.setEmail("ali@example.com");

        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Member result = memberService.createMember(member);

        assertNotNull(result);
        assertEquals("Ali", result.getFirstName());
        assertEquals("Mammadov", result.getLastName());
        assertEquals("ali@example.com", result.getEmail());

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void getMemberById_shouldReturnMemberWhenMemberExists() {
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Ali");

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ali", result.getFirstName());
    }

    @Test
    void getMemberById_shouldThrowExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> memberService.getMemberById(1L)
        );
    }

    @Test
    void deleteMember_shouldDeleteMemberWhenNoLoanHistoryExists() {
        Member member = new Member();
        member.setId(1L);

        when(memberRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(member));

        when(loanRepository.existsByMember_Id(1L))
                .thenReturn(false);

        memberService.deleteMember(1L);

        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMember_shouldThrowExceptionWhenLoanHistoryExists() {
        Member member = new Member();
        member.setId(1L);

        when(memberRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(member));

        when(loanRepository.existsByMember_Id(1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> memberService.deleteMember(1L)
        );

        verify(memberRepository, never()).delete(any(Member.class));
    }

    @Test
    void deleteMember_shouldThrowExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findByIdWithLock(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> memberService.deleteMember(1L)
        );

        verify(memberRepository, never()).delete(any(Member.class));
    }
}
