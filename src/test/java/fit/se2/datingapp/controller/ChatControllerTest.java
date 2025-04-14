package fit.se2.datingapp.controller;

import fit.se2.datingapp.dto.GetMessageResponseDTO;
import fit.se2.datingapp.dto.SendMessageDTO;
import fit.se2.datingapp.dto.SendMessageResponseDTO;
import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.MessageService;
import fit.se2.datingapp.service.UserPhotoService;
import fit.se2.datingapp.service.UserService;
import fit.se2.datingapp.websocket.SocketMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ChatControllerTest {

    @Mock
    private MatchingService matchingService;

    @Mock
    private UserService userService;

    @Mock
    private UserPhotoService userPhotoService;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendMessage_Success() {
        Long senderId = 1L;
        Long receiverId = 2L;
        String content = "Test for send message success!";

        User mockSender = mock(User.class);
        User mockReceiver = mock(User.class);

        when(mockSender.getId()).thenReturn(senderId);
        when(mockSender.getEmail()).thenReturn("sender@gmail.com");
        when(mockReceiver.getId()).thenReturn(receiverId);
        when(mockReceiver.getEmail()).thenReturn("receiver@gmail.com");

        when(userService.getUserById(senderId)).thenReturn(mockSender);
        when(userService.getUserById(receiverId)).thenReturn(mockReceiver);

        when(matchingService.isAMatchBetween(mockSender, mockReceiver)).thenReturn(true);

        SendMessageDTO sendMessageDTO = SendMessageDTO.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .build();

        Message createdMessage = new Message();
        createdMessage.setDate(LocalDateTime.now());
        when(messageService.create(any(Message.class))).thenReturn(createdMessage);

        ResponseEntity<SendMessageResponseDTO> response = chatController.sendMessage(sendMessageDTO, mockSender);

        verify(template).convertAndSendToUser(eq("sender@gmail.com"), eq("/queue/chat"), any(SocketMessage.class));
        verify(template).convertAndSendToUser(eq("receiver@gmail.com"), eq("/queue/chat"), any(SocketMessage.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSendMessage_Failure_InvalidReceiver() {
        Long senderId = 1L;
        Long receiverId = 2L;
        String content = "Test for send message failure!";

        User mockSender = mock(User.class);
        when(mockSender.getId()).thenReturn(senderId);
        when(userService.getUserById(receiverId)).thenReturn(null);

        SendMessageDTO sendMessageDTO = SendMessageDTO.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .build();

        ResponseEntity<SendMessageResponseDTO> response = chatController.sendMessage(sendMessageDTO, mockSender);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetMessage_Success() {
        Long senderId = 1L;
        Long receiverId = 2L;
        User mockSender = mock(User.class);
        User mockReceiver = mock(User.class);
        when(mockSender.getId()).thenReturn(senderId);
        when(mockReceiver.getId()).thenReturn(receiverId);
        when(userService.getUserById(receiverId)).thenReturn(mockReceiver);
        when(matchingService.isAMatchBetween(mockSender, mockReceiver)).thenReturn(true);

        List<Message> mockMessages = List.of(
                Message.builder()
                        .sender(mockSender)
                        .receiver(mockReceiver)
                        .content("Test for get message success!")
                        .date(LocalDateTime.now())
                        .build(),
                Message.builder()
                        .sender(mockSender)
                        .receiver(mockReceiver)
                        .content("Test for get message failure!")
                        .date(LocalDateTime.now())
                        .build()
        );

        when(messageService.getMessagesByRange(mockSender, mockReceiver, 0, 10)).thenReturn(mockMessages);

        ResponseEntity<GetMessageResponseDTO> response = chatController.getMessage(0, 10, receiverId, mockSender);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockMessages.size(), response.getBody().getMessages().size());
    }

    @Test
    public void testGetMessage_Failure_InvalidReceiver() {
        Long senderId = 1L;
        Long receiverId = 2L;
        User mockSender = mock(User.class);
        when(mockSender.getId()).thenReturn(senderId);
        when(userService.getUserById(receiverId)).thenReturn(null);

        ResponseEntity<GetMessageResponseDTO> response = chatController.getMessage(0, 10, receiverId, mockSender);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddUser_Success() {
        Long senderId = 1L;
        Long receiverId = 2L;
        SocketMessage socketMessage = SocketMessage.builder()
                .sender(senderId)
                .receiver(receiverId)
                .content("Hello!")
                .type(SocketMessage.MessageType.CHAT)
                .build();

        SimpMessageHeaderAccessor mockAccessor = mock(SimpMessageHeaderAccessor.class);
        when(mockAccessor.getSessionAttributes()).thenReturn(new HashMap<>());

        User mockSender = mock(User.class);
        User mockReceiver = mock(User.class);
        when(userService.getUserById(senderId)).thenReturn(mockSender);
        when(userService.getUserById(receiverId)).thenReturn(mockReceiver);
        when(mockSender.getEmail()).thenReturn("sender@example.com");
        when(mockReceiver.getEmail()).thenReturn("receiver@example.com");

        chatController.addUser(socketMessage, mockAccessor);

        verify(template).convertAndSendToUser(eq("sender@example.com"), eq("/queue/chat"), eq(socketMessage));
        verify(template).convertAndSendToUser(eq("receiver@example.com"), eq("/queue/chat"), eq(socketMessage));
        verify(mockAccessor).getSessionAttributes();
    }

    @Test
    public void testGetChatPage_Success() {
        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            Long targetUserId = 2L;
            User mockUser = mock(User.class);
            User mockTargetUser = mock(User.class);

            when(mockUser.getId()).thenReturn(1L);
            when(mockTargetUser.getId()).thenReturn(targetUserId);

            mockedStatic.when(UserService::getCurrentUser).thenReturn(mockUser);

            when(userService.getUserById(targetUserId)).thenReturn(mockTargetUser);
            when(matchingService.isAMatchBetween(mockUser, mockTargetUser)).thenReturn(true);

            UserPhoto mockPhoto = UserPhoto.builder()
                    .id(1L)
                    .user(mockTargetUser)
                    .photoUrl("mock-url")
                    .isProfilePicture(true)
                    .uploadDate(LocalDateTime.now())
                    .build();
            when(userPhotoService.getUserAvatar(mockTargetUser)).thenReturn(mockPhoto);

            Model mockModel = mock(Model.class);

            String viewName = chatController.getChatPage(targetUserId.toString(), mockModel);

            verify(mockModel).addAttribute("uid", targetUserId.toString());
            verify(mockModel).addAttribute("targetUser", mockTargetUser);
            verify(mockModel).addAttribute("targetAvatar", "mock-url");
            assertEquals("chat", viewName);
        }
    }

    @Test
    public void testGetChatPage_Failure_NoMatch() {
        Long targetUserId = 2L;
        User mockUser = mock(User.class);
        User mockTargetUser = mock(User.class);

        try (MockedStatic<UserService> mockedStatic = mockStatic(UserService.class)) {
            when(mockUser.getId()).thenReturn(1L);
            when(mockTargetUser.getId()).thenReturn(targetUserId);

            mockedStatic.when(UserService::getCurrentUser).thenReturn(mockUser);

            when(userService.getUserById(targetUserId)).thenReturn(mockTargetUser);
            when(matchingService.isAMatchBetween(mockUser, mockTargetUser)).thenReturn(false);

            Model mockModel = mock(Model.class);

            String viewName = chatController.getChatPage(targetUserId.toString(), mockModel);

            assertEquals("redirect:/", viewName);
        }
    }
}
