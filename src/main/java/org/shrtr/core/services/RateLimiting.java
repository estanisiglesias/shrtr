package org.shrtr.core.services;

import lombok.RequiredArgsConstructor;
import org.shrtr.core.domain.entities.Link;
import org.shrtr.core.domain.entities.User;
import org.springframework.stereotype.Service;

public interface RateLimiting {
    boolean hasExceedRedirect(String shortened, User user);
}
