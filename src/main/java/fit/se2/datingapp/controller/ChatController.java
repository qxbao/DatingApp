package fit.se2.datingapp.controller;

import fit.se2.datingapp.constants.Const;
import fit.se2.datingapp.dto.GetMessageResponseDTO;
import fit.se2.datingapp.dto.SendMessageDTO;
import fit.se2.datingapp.dto.SendMessageResponseDTO;
import fit.se2.datingapp.model.Message;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.repository.MessageRepository;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.MessageService;
import fit.se2.datingapp.service.UserPhotoService;
import fit.se2.datingapp.service.UserService;
import fit.se2.datingapp.websocket.SocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final MatchingService matchingService;
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final SimpMessagingTemplate template;
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatController(MatchingService matchingService, UserService userService, UserPhotoService userPhotoService, SimpMessagingTemplate template, MessageService messageService, MessageRepository messageRepository) {
        this.matchingService = matchingService;
        this.userService = userService;
        this.userPhotoService = userPhotoService;
        this.template = template;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/sendMessage")
    @SendTo("/queue/chat")
    public ResponseEntity<SendMessageResponseDTO> sendMessage(
            @RequestBody SendMessageDTO sendMessageDTO,
            @ModelAttribute("user") User user
    ) {
        User receiver = userService.getUserById(sendMessageDTO.getReceiverId());
        if (Objects.equals(
                sendMessageDTO.getSenderId(), null) ||
                receiver == null ||
                Objects.equals(sendMessageDTO.getReceiverId(), null) ||
                !sendMessageDTO.getSenderId().equals(user.getId()) ||
                !matchingService.isAMatchBetween(user, receiver)
                )  {
            return ResponseEntity.badRequest().body(null);
        }
        Message created = messageService.create(
                    Message
                        .builder()
                            .sender(user)
                            .receiver(receiver)
                            .content(sendMessageDTO.getContent())
                        .build());
        List<Long> matchedUsers = List.of(sendMessageDTO.getSenderId(), sendMessageDTO.getReceiverId());
        for (String email : matchedUsers.stream().map(userService::getUserById).map(User::getEmail).toList()) {
            template.convertAndSendToUser(
                    email,
                    "/queue/chat",
                    SocketMessage.builder()
                        .sender(user.getId())
                        .receiver(receiver.getId())
                        .content(sendMessageDTO.getContent().trim())
                        .type(SocketMessage.MessageType.CHAT)
                        .build()
            );
        }
        return ResponseEntity.ok(
                SendMessageResponseDTO
                    .builder()
                    .timestamp(created.getDate().atZone(ZoneId.systemDefault()).toEpochSecond())
                    .build());
    }

    @GetMapping(value = "/getMessage")
    public ResponseEntity<GetMessageResponseDTO> getMessage(
            @RequestParam int fromIndex,
            @RequestParam int toIndex,
            @RequestParam  Long receiverId,
            @ModelAttribute("user") User user
            ) {
        User receiver = userService.getUserById(receiverId);
        if (receiver == null ||
                user == null ||
                !matchingService.isAMatchBetween(user, receiver)){
            return ResponseEntity.badRequest().body(null);

        }
        int offset = toIndex - fromIndex;
        List<Message> messages = messageRepository.getMessagesByRange(user, receiver, fromIndex, offset);
        return ResponseEntity.ok(
                GetMessageResponseDTO.builder()
                    .messages(messages.stream().map(Message::getContent).toList())
                    .senders(messages.stream().map(m -> m.getSender().getId()).toList())
                    .isOverflow(messages.size() < offset)
                .build());
    }

    @MessageMapping(value="chat.addUser")
    @SendTo("/queue/chat")
    public void addUser(@Payload SocketMessage socketMessage, SimpMessageHeaderAccessor accessor) {
        List<Long> matchedUsers = List.of(socketMessage.getSender(), socketMessage.getReceiver());
        for (Long userId : matchedUsers) {
            template.convertAndSendToUser(
                userService.getUserById(userId).getEmail(),
                "/queue/chat",
                    socketMessage
            );
        }
        Objects.requireNonNull(accessor.getSessionAttributes()).put("uid", socketMessage.getSender());
    }
    @GetMapping(value ="/{uid}")
    public String getChatPage(@PathVariable String uid, Model model) {
        User user = UserService.getCurrentUser();
        if (user == null) return "redirect:/login";
        User targetUser = userService.getUserById(Long.parseLong(uid));
        if (matchingService.isAMatchBetween(
                user, targetUser)) {
            UserPhoto targetAvatar = userPhotoService.getUserAvatar(targetUser);
            model.addAttribute("uid", uid);
            model.addAttribute("targetUser", targetUser);
            model.addAttribute("targetAvatar", targetAvatar == null ? Const.DEFAULT_AVATAR_URL : targetAvatar.getPhotoUrl());
            return "chat";
        } else {
            return "redirect:/";
        }
    }
}