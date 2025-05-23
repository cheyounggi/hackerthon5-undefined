package org.server.core.metric.domain;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetricRepository extends JpaRepository<Metric, Long> {
    List<Metric> findAllByMemberIdAndMetadataRequestAtAfter(Long memberId, Instant requestAt);

    @Query(""" 
                select new org.server.core.metric.domain.ActiveHourEntry(hour(m.metadata.requestAt), count(m))
                 from Metric m
                    where m.memberId=:memberId
                    and m.metadata.requestAt between :start and :end
                 group by hour(m.metadata.requestAt)
                 order by hour(m.metadata.requestAt)
            """)
    List<ActiveHourEntry> aggregate24HourUsageBy(
            @Param("memberId") Long memberId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("""
            select new org.server.core.metric.domain.ActiveDomainEntry(m.siteDomain, count(m))
                 from Metric m
                    where m.memberId = :memberId
                    and m.metadata.requestAt between :start and :end
                 group by m.siteDomain.id
            """)
    List<ActiveDomainEntry> aggregate24HourUsageDomainBy(
            @Param("memberId") Long memberId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("""
                select new org.server.core.metric.domain.ActiveDomainPathEntry(m.siteDomain, m.metadata.path, count(m))
                 from Metric m
                    where m.memberId = :memberId
                    and m.metadata.requestAt between :start and :end
                  group by m.siteDomain.id, m.metadata.path
            """)
    List<ActiveDomainPathEntry> aggregate24HourUsageDomainTraceBy(
            @Param("memberId") Long memberId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

}
