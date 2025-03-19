package fit.se2.datingapp.controller;

import fit.se2.datingapp.websocket.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {

    @MessageMapping("sendMessage")
    @SendTo("/topic/chat")
    public Message sendMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("addUser")
    @SendTo("/topic/chat")
    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor accessor) {
        accessor.getSessionAttributes().put("uid", message.getSender());
        return message;
    }
    @GetMapping(value ="/chat/{uid}")
    public String getChatPage(@PathVariable String uid, Model model) {
        model.addAttribute("uid", uid);
        return "chat";
    }
}