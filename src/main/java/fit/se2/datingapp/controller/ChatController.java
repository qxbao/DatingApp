package fit.se2.datingapp.controller;

import fit.se2.datingapp.constants.Const;
import fit.se2.datingapp.model.User;
import fit.se2.datingapp.model.UserPhoto;
import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.UserPhotoUtilityService;
import fit.se2.datingapp.service.UserUtilityService;
import fit.se2.datingapp.websocket.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;

@Controller
public class ChatController {

    private final MatchingService matchingService;
    private final UserUtilityService userUtilityService;
    private final UserPhotoUtilityService userPhotoUtilityService;

    public ChatController(MatchingService matchingService, UserUtilityService userUtilityService, UserPhotoUtilityService userPhotoUtilityService) {
        this.matchingService = matchingService;
        this.userUtilityService = userUtilityService;
        this.userPhotoUtilityService = userPhotoUtilityService;
    }

    @MessageMapping("sendMessage")
    @SendTo("/topic/chat")
    public Message sendMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("addUser")
    @SendTo("/topic/chat")
    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor accessor) {
        Objects.requireNonNull(accessor.getSessionAttributes()).put("uid", message.getSender());
        return message;
    }
    @GetMapping(value ="/chat/{uid}")
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