
package az.abbtech.librarymanagementapi.controller;

import az.abbtech.librarymanagementapi.model.entity.Member;
import az.abbtech.librarymanagementapi.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    void createMember_shouldReturnCreated() throws Exception {
        Member member = createMember();

        when(memberService.createMember(any(Member.class))).thenReturn(member);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Ali",
                                    "lastName": "Mammadov",
                                    "email": "ali@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.lastName").value("Mammadov"))
                .andExpect(jsonPath("$.email").value("ali@example.com"));

        verify(memberService).createMember(any(Member.class));
    }

    @Test
    void getAllMembers_shouldReturnMemberList() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of(createMember()));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ali"))
                .andExpect(jsonPath("$[0].lastName").value("Mammadov"));

        verify(memberService).getAllMembers();
    }

    @Test
    void getMemberById_shouldReturnMember() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(createMember());

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.email").value("ali@example.com"));

        verify(memberService).getMemberById(1L);
    }

    @Test
    void updateMember_shouldReturnUpdatedMember() throws Exception {
        Member member = createMember();
        member.setFirstName("Murad");

        when(memberService.updateMember(eq(1L), any(Member.class))).thenReturn(member);

        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Murad",
                                    "lastName": "Mammadov",
                                    "email": "murad@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Murad"));

        verify(memberService).updateMember(eq(1L), any(Member.class));
    }

    @Test
    void deleteMember_shouldReturnNoContent() throws Exception {
        doNothing().when(memberService).deleteMember(1L);

        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMember(1L);
    }

    @Test
    void createMember_shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Ali",
                                    "lastName": "Mammadov",
                                    "email": "invalid-email"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).createMember(any(Member.class));
    }

    private Member createMember() {
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Ali");
        member.setLastName("Mammadov");
        member.setEmail("ali@example.com");
        return member;
    }
}
