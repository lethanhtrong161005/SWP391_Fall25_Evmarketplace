package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.scheduler;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsignmentExpiryJob {

//    private final ConsignmentRequestRepository consignmentRequestRepository;
////    sau 7 ngày được duyệt nhưng không đá động -> hết hạn
//    @Transactional
//    @Scheduled(cron = "${jobs.expired.cron: 0 */1 0 * * *}", zone = "Asia/Ho_Chi_Minh")
//    public void runDailyExpiry() {
//        int requestExpired = consignmentRequestRepository.expiredRequest(
//                EnumSet.of(ConsignmentRequestStatus.REQUEST_REJECTED,
//                        ConsignmentRequestStatus.RESCHEDULED,
//                        ConsignmentRequestStatus.SCHEDULING));
////        log.info("[ConsignmentExpiryJob] expiredRequest={}",requestExpired);
//    }

}
