package fit.se2.datingapp.repository;

import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (" +
            "(m.sender = :a AND m.receiver = :b) OR " +
            "(m.sender = :b AND m.receiver = :a)) ORDER BY " +
            "m.date DESC " +
            "LIMIT :offset OFFSET :fromIndex")
    List<Message> getMessagesByRange(User a, User b, int fromIndex, int offset);
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
            "ORDER BY m.date ASC")
    List<Message> findMessagesBetweenUsers(Long user1Id, Long user2Id);

}