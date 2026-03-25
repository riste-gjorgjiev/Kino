package mk.ukim.finki.ecommerce.ecommercelab.scheduler;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterializedViewRefreshScheduler {
    private final EntityManager entityManager;

    @Scheduled(fixedRateString = "${library.matview.refresh-interval-ms:60000}")
    public void refreshMaterializedView(){
        log.info("Materialized view refresh skipped - running on H2 (regular view, no refresh)");
    }
}
