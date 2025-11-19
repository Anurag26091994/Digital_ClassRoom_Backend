package com.digital.repository;

import com.digital.entity.Book;
import com.digital.entity.BookIssue;
import com.digital.entity.LibraryMember;

import com.digital.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByMemberAndStatus(LibraryMember member, IssueStatus status);
    List<BookIssue> findByStatus(IssueStatus status);

    List<BookIssue> findByMember(LibraryMember member);

    long countByBookAndStatus(Book book, IssueStatus issueStatus);


//    List<BookIssue> findByMemberAndStatus(LibraryMember mem, IssueStatus issueStatus);
}
