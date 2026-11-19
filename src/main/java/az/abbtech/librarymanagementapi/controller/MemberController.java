
package az.abbtech.librarymanagementapi.controller;

import az.abbtech.librarymanagementapi.model.dto.request.MemberRequest;
import az.abbtech.librarymanagementapi.model.dto.response.MemberResponse;
import az.abbtech.librarymanagementapi.model.entity.Member;
import az.abbtech.librarymanagementapi.mapper.MemberMapper;
import az.abbtech.librarymanagementapi.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(
            @Valid @RequestBody MemberRequest request
    ) {

        Member member = MemberMapper.toEntity(request);

        Member savedMember = memberService.createMember(member);

        return MemberMapper.toResponse(savedMember);
    }

    @GetMapping
    public List<MemberResponse> getAllMembers() {

        return memberService.getAllMembers()
                .stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public MemberResponse getMemberById(@PathVariable Long id) {

        Member member = memberService.getMemberById(id);

        return MemberMapper.toResponse(member);
    }

    @PutMapping("/{id}")
    public MemberResponse updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request
    ) {

        Member member = MemberMapper.toEntity(request);

        Member updatedMember = memberService.updateMember(id, member);

        return MemberMapper.toResponse(updatedMember);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {

        memberService.deleteMember(id);
    }
}
