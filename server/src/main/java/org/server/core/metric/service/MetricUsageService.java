package org.server.core.metric.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.server.core.metric.api.payload.response.ActiveSiteDomainEntry;
import org.server.core.metric.domain.ActiveDomainPathEntry;
import org.server.core.metric.domain.ActiveHourEntry;
import org.server.core.metric.domain.ActiveSiteDomainTraceEntry;
import org.server.core.metric.domain.MetricRepository;
import org.server.global.utils.LocalDateToInstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricUsageService {
    private static final Map<Integer, ActiveHourEntry> CACHED_HOURS = Collections.unmodifiableMap(
            Stream.iterate(0, i -> i + 1)
                    .limit(23)
                    .collect(Collectors.toMap(Function.identity(), hour -> new ActiveHourEntry(hour, 0)))
    );

    private final MetricRepository metricRepository;

    // 24시간 사용한 도메인 집계
    public List<ActiveSiteDomainTraceEntry> aggregateByDailyUsageTrace(Long userId, LocalDate date) {
        var range = LocalDateToInstant.getBoundaryInstants(date, LocalDateToInstant.SEOUL_ZONE);

        var metrics = metricRepository.aggregate24HourUsageDomainTraceBy(userId, range.start(), range.end());

        var siteDomainListMap = metrics.stream()
                .collect(Collectors.groupingBy(ActiveDomainPathEntry::siteDomain));

        return siteDomainListMap.entrySet()
                .stream()
                .map(ActiveSiteDomainTraceEntry::from)
                .toList();
    }

    // 24시간 사용량 집계
    public List<ActiveHourEntry> aggregateByDailyUsage(Long userId, LocalDate date) {
        var range = LocalDateToInstant.getBoundaryInstants(date, LocalDateToInstant.SEOUL_ZONE);

        var metrics = metricRepository.aggregate24HourUsageBy(userId, range.start(), range.end());

        return CACHED_HOURS.entrySet()
                .stream()
                .map(entry -> metrics.stream()
                        .filter(metric -> metric.hour() == entry.getKey())
                        .findFirst()
                        .orElse(entry.getValue()))
                .sorted(Comparator.comparingInt(ActiveHourEntry::hour))
                .toList();
    }

    public List<ActiveSiteDomainEntry> aggregateByDomainDailyUsage(Long userId, LocalDate date) {
        var range = LocalDateToInstant.getBoundaryInstants(date, LocalDateToInstant.SEOUL_ZONE);

        var metrics = metricRepository.aggregate24HourUsageDomainBy(userId, range.start(), range.end());

        return metrics.stream()
                .map(ActiveSiteDomainEntry::from)
                .toList();
    }
}
