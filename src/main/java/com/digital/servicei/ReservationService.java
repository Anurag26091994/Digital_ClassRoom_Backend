package com.digital.servicei;


import com.digital.dto.ReservationDTO;

import java.util.List;

public interface ReservationService {
    ReservationDTO reserveBook(Long bookId, Long memberId);
    ReservationDTO approveReservation(Long reservationId);
    ReservationDTO cancelReservation(Long reservationId);
    List<ReservationDTO> listReservationsByMember(Long memberId);
    List<ReservationDTO> listActiveReservationsForBook(Long bookId);
}
