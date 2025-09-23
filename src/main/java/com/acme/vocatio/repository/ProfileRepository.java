package com.acme.vocatio.repository;

import com.acme.vocatio.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("SELECT a FROM Profile a WHERE a.name = :name")
    Optional<Profile> findByName(@Param("name") String n);

    @Query("SELECT a FROM Profile a ORDER BY a.name ASC")
    List<Profile> findAllByOrderByNameAsc();

    //Consultar los users de un id de Profile
    @Query("SELECT b FROM Profile b WHERE b.User.id=:userId")
    List<User> findByProfileId(Long ProfileId);
    @Query(value = "SELECT * FROM Users WHERE Profile_id=:ProfileId"
            ,nativeQuery = true)
    List<User> findByProfileIdSQL(Long ProfileId);

    //Consultar los users por nombre de Profile
    @Query(value = """
            SELECT b.*
            FROM Users b
            JOIN Profiles a ON a.id = b.Profile_id
            WHERE a.name = :ProfileName
                """,
            nativeQuery = true)
    List<User> findByProfileIdSQL(String id_usuario);
}
