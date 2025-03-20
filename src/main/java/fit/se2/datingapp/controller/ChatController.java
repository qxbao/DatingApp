package fit.se2.datingapp.controller;

import fit.se2.datingapp.constants.Const;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.UserPhotoUtilityService;
import fit.se2.datingapp.service.UserUtilityService;
import fit.se2.datingapp.websocket.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final MatchingService matchingService;
    private final UserUtilityService userUtilityService;
    private final UserPhotoUtilityService userPhotoUtilityService;
    private final SimpMessagingTemplate template;

    @Autowired
    public ChatController(MatchingService matchingService, UserUtilityService userUtilityService, UserPhotoUtilityService userPhotoUtilityService, SimpMessagingTemplate template) {
        this.matchingService = matchingService;
        this.userUtilityService = userUtilityService;
        this.userPhotoUtilityService = userPhotoUtilityService;
        this.template = template;
    }

    @MessageMapping(value="chat.sendMessage")
    @SendTo("/queue/chat")
    public void sendMessage(@Payload Message message) {
        List<Long> matchedUsers = List.of(message.getSender(), message.getReceiver());
        for (String email : matchedUsers.stream().map(userUtilityService::getUserById).map(User::getEmail).toList()) {
            System.out.println(email);
            template.convertAndSendToUser(
                email,
                "/queue/chat",
                message
            );
        }
    }

    @MessageMapping(value="chat.addUser")
    @SendTo("/queue/chat")
    public void addUser(@Payload Message message, SimpMessageHeaderAccessor accessor) {
        List<Long> matchedUsers = List.of(message.getSender(), message.getReceiver());
        for (Long userId : matchedUsers) {
            template.convertAndSendToUser(
                userUtilityService.getUserById(userId).getEmail(),
                "/queue/chat",
                message
            );
        }
        Objects.requireNonNull(accessor.getSessionAttributes()).put("uid", message.getSender());
    }
    @GetMapping(value ="/{uid}")
    public String getChatPage(@PathVariable String uid, Model model) {
        User user = UserUtilityService.getCurrentUser();
        if (user == null) return "redirect:/login";
        User targetUser = userUtilityService.getUserById(Long.parseLong(uid));
        if (matchingService.isAMatchBetween(
                user, targetUser)) {
            UserPhoto targetAvatar = userPhotoUtilityService.getUserAvatar(targetUser);
            model.addAttribute("uid", uid);
            model.addAttribute("targetUser", targetUser);
            model.addAttribute("targetAvatar", targetAvatar == null ? Const.DEFAULT_AVATAR_URL : targetAvatar.getPhotoUrl());
            return "chat";
        } else {
            return "redirect:/";
        }
    }
}