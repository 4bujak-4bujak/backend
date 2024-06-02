package com.example.sabujak.reservation.service;

import com.example.sabujak.space.repository.FocusDeskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationScheduledService {

    private final FocusDeskRepository focusDeskRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void endAllOfFocusDeskAtMidnight() {
        focusDeskRepository.findAllByCanReserve(false)
                .forEach(focusDesk -> focusDesk.changeCanReserve(true));
    }
}
