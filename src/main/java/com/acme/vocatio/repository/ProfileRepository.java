package com.acme.vocatio.repository;

import com.acme.vocatio.model.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByNameIgnoreCase(String name);

    List<Profile> findAllByOrderByNameAsc();

    List<Profile> findByNameContainingIgnoreCase(String name);
}
