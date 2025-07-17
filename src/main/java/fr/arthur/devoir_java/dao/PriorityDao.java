package fr.arthur.devoir_java.dao;

import fr.arthur.devoir_java.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PriorityDao extends JpaRepository<Priority, Integer> {
    Optional<Priority> findByName(String name);
}
