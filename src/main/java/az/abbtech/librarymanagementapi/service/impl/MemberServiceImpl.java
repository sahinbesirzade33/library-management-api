
package az.abbtech.librarymanagementapi.service.impl;

import az.abbtech.librarymanagementapi.exception.BusinessException;
import az.abbtech.librarymanagementapi.exception.ResourceNotFoundException;
import az.abbtech.librarymanagementapi.model.entity.Member;
import az.abbtech.librarymanagementapi.repository.LoanRepository;
import az.abbtech.librarymanagementapi.repository.MemberRepository;
import az.abbtech.librarymanagementapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final MessageSource messageSource;

    @Override
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "member.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));
    }

    @Override
    public Member updateMember(Long id, Member updatedMember) {

        Member member = memberRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "member.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        member.setFirstName(updatedMember.getFirstName());
        member.setLastName(updatedMember.getLastName());
        member.setEmail(updatedMember.getEmail());

        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(Long id) {

        Member member = memberRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "member.not-found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        if (loanRepository.existsByMember_Id(id)) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "member.has-loan-history",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        memberRepository.delete(member);
    }
}
