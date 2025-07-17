package fr.arthur.devoir_java.dao;

import fr.arthur.devoir_java.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketDao extends JpaRepository<Ticket, Integer> {
}
