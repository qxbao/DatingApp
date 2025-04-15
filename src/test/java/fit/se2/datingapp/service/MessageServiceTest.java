package fit.se2.datingapp.service;

import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sender = User.builder().id(1L).name("Nam Vu").build();
        receiver = User.builder().id(2L).name("Nguyen Kim Dinh").build();
    }

    @Test
    public void testCreateMessage_Success() {
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content("Hi")
                .build();

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setId(1L);
            return savedMessage;
        });

        Message result = messageService.create(message);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hi", result.getContent());
        assertNotNull(result.getDate());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    public void testGetMessagesByRange() {
        List<Message> mockMessages = List.of(
                Message.builder().content("Hello").sender(sender).receiver(receiver).build(),
                Message.builder().content("How are you?").sender(sender).receiver(receiver).build()
        );

        when(messageRepository.getMessagesByRange(sender, receiver, 0, 2)).thenReturn(mockMessages);

        List<Message> result = messageService.getMessagesByRange(sender, receiver, 0, 2);

        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0).getContent());
        assertEquals("How are you?", result.get(1).getContent());
        verify(messageRepository).getMessagesByRange(sender, receiver, 0, 2);
    }
}
