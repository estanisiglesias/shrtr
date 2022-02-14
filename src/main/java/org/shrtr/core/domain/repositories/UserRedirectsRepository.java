package org.shrtr.core.domain.repositories;

import org.shrtr.core.domain.entities.User;
import org.shrtr.core.domain.entities.Link;
import org.shrtr.core.domain.entities.UserRedirects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRedirectsRepository extends JpaRepository<UserRedirects, UUID> {
    List<UserRedirects> findAllByDateBetweenAndUserAndLink(LocalDateTime from, LocalDateTime to, User user, Link link);
}
