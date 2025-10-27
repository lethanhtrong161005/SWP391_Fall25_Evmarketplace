package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.scheduler;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.InspectionScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoShowScheduler {

    private final InspectionScheduleRepository repo;

    @Value("${jobs.no_show.grace_minutes:30}")
    private int graceMinutes;

    // sau khi kết thúc ca hẹn vẫn không checkin -> no_show
    // chạy mỗi 5 phút
    @Scheduled(cron = "${jobs.no_show.cron:0 */5 * * * *}")
    public void run(){
        int affected = repo.markNoShow(graceMinutes);
    }


}
