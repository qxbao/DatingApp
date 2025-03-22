package fit.se2.datingapp.service;

import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message create(Message message) {
        message.setDate(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByRange(User sender, User receiver, int fromIndex, int offset) {
        return messageRepository.getMessagesByRange(sender, receiver, fromIndex, offset);
    }
}
