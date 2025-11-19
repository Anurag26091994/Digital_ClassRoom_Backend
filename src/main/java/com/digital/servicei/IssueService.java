package com.digital.servicei;


import com.digital.dto.IssueDTO;

import java.util.List;

public interface IssueService {
    IssueDTO issueBook(Long bookId, Long memberId);
    IssueDTO returnBook(Long issueId);
    IssueDTO renewBook(Long issueId);
    List<IssueDTO> listIssuesByMember(Long memberId);
    List<IssueDTO> listOverdueIssues();
}
