package com.needed.task.specification;

import org.springframework.data.jpa.domain.Specification;

import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;

public class AlertSpecification {
    // Пустое условие — "всё подходит"
    private static Specification<Alert> empty() {
        return (root, query, cb) -> cb.conjunction(); // всегда true
    }

    public static Specification<Alert> hasStatus(StatusType status) {
        if (status == null) return empty();
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Alert> hasBusId(Long busId) {
        if (busId == null) return empty();
        return (root, query, cb) -> cb.equal(root.get("busId"), busId);
    }

    public static Specification<Alert> locationContains(String location) {
        if (location == null || location.trim().isEmpty()) return empty();
        String pattern = "%" + location.trim().toLowerCase() + "%";
        return (root, query, cb) -> 
            cb.like(cb.lower(root.get("location")), pattern);
    }

    // Комбинируем — безопасно и просто
    public static Specification<Alert> filter(StatusType status, Long busId, String location) {
        return hasStatus(status)
                .and(hasBusId(busId))
                .and(locationContains(location));
    }
}
