package org.shrtr.core.services;

import lombok.*;
import org.shrtr.core.domain.entities.Link;
import org.shrtr.core.domain.entities.User;
import org.shrtr.core.domain.repositories.LinksRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InMemoryRateLimiting implements RateLimiting {

    private final LinksRepository linksRepository;
    HashMap<UUID, Pair> linksRequests = new HashMap<UUID, Pair>();

    @Getter
    @Setter
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class Pair {
        private LocalDateTime windowStart;
        private Long requests;
    }

    public boolean hasExceedRedirect(String shortened, User user) {
        boolean exceeded = false;
        Optional<Link> byShortened = linksRepository.findByShortened(shortened);
        Link link = byShortened.get();

        if (linksRequests == null){
            // first redirect ever
            linksRequests.put(link.getId(), new Pair(LocalDateTime.now(), 1L));
        }

        var linkRequests = linksRequests.get(link.getId());

        if (Duration.between(linkRequests.windowStart, LocalDateTime.now()).toMillis() > user.getMaxRequestsTimeWindowMs()) {
            // expired window
            linkRequests.setRequests(1L);
            linkRequests.setWindowStart( LocalDateTime.now());
        }
        else {
            // current window
            if (linkRequests.getRequests() >= user.getMaxRequests()){
                exceeded = true;
            }
            linkRequests.setRequests(linkRequests.getRequests() + 1);
        }

        return exceeded;
    }
}