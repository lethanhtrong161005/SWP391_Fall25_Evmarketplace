package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.scheduler;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentAgreement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentAgreementStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.DepositStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentAgreementRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentRequestRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsignmentExpiryJob {

    private final ConsignmentRequestRepository consignmentRequestRepository;
    private final ConsignmentAgreementRepository consignmentAgreementRepository;

    //    sau 7 ngày được duyệt nhưng không đá động -> hết hạn
    @Transactional
    @Scheduled(cron = "${jobs.expired.cron: 0 30 0 * * *}", zone = "Asia/Ho_Chi_Minh")
    public void runDailyExpiry() {
        EnumSet<ConsignmentRequestStatus> statuses = EnumSet.of(
                ConsignmentRequestStatus.REQUEST_REJECTED,
                ConsignmentRequestStatus.RESCHEDULED,
                ConsignmentRequestStatus.SCHEDULING
        );

        // Convert enum to string for native query
        List<String> statusStrings = statuses.stream()
                .map(Enum::name)
                .toList();

        int requestExpired = consignmentRequestRepository.expiredRequest(statusStrings);
        log.info("Statuses to expire: {}", statuses);
        log.info("Expired {} requests", requestExpired);

    }


    //hợp đồng hết hạn
    @Transactional
    @Scheduled(cron = "0 * * * * *") // chạy mỗi giây một lần
    public void autoExpireAgreements() {
        List<ConsignmentAgreement> expiredList = consignmentAgreementRepository.findExpiredAgreements();

        if (expiredList.isEmpty()) {
            log.info("[Scheduler] No expired agreements found.");
            return;
        }

        log.info("[Scheduler] Found {} agreements to expire.", expiredList.size());

        for (ConsignmentAgreement agreement : expiredList) {
            try {
                agreement.setStatus(ConsignmentAgreementStatus.EXPIRED);

                // Cập nhật trạng thái request nếu bạn muốn (tùy logic)
                ConsignmentRequest request = agreement.getRequest();
                request.setStatus(ConsignmentRequestStatus.EXPIRED);

                consignmentAgreementRepository.save(agreement);
                consignmentRequestRepository.save(request);

                log.info("[Scheduler] Expired agreement id={}, requestId={}",
                        agreement.getId(), request.getId());
            } catch (Exception e) {
                log.error("[Scheduler] Failed to expire agreement id={}", agreement.getId(), e);
            }
        }
    }

}
