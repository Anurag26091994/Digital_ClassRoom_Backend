package com.digital.repository;

import com.digital.entity.Event;
import com.digital.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventTypeAndEventDate(EventType eventType, LocalDate holidayDate);
}
