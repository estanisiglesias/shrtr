package org.shrtr.core.services;

import lombok.RequiredArgsConstructor;
import org.shrtr.core.domain.entities.Link;
import org.shrtr.core.domain.entities.User;
import org.shrtr.core.domain.entities.UserRedirects;
import org.shrtr.core.domain.repositories.LinksRepository;
import org.shrtr.core.domain.repositories.UserRedirectsRepository;
import org.shrtr.core.domain.repositories.UsersRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Primary
@Service
@RequiredArgsConstructor
public class MariaDBRateLimiting implements RateLimiting {
    private final UserRedirectsRepository userRedirectsRepository;
    private final LinksRepository linksRepository;
    @Override
    public boolean hasExceedRedirect(String shortened, User user) {
        boolean exceed = false;
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minus(user.getMaxRequestsTimeWindowMs(), ChronoUnit.MILLIS);
        Optional<Link> byShortened = linksRepository.findByShortened(shortened);
        Link link = byShortened.get();
        List<UserRedirects> userRedirects = userRedirectsRepository.findAllByDateBetweenAndUserAndLink(from, to, user, link);
        if (userRedirects.size()>=user.getMaxRequests()) {
            exceed = true;
        }
        else
        {
            UserRedirects ur = new UserRedirects();
            ur.setUser(user);
            ur.setLink(link);
            userRedirectsRepository.save(ur);
        }
        return exceed;
    }
}
