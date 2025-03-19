package fit.se2.datingapp.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageOperations;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String uid = (String) accessor.getSessionAttributes().get("uid");
        if  (uid != null) {
            log.info("User Disconnected: {} ", uid);
            messageOperations.convertAndSend("/topic/public", Message.builder()
                    .type(Message.MessageType.LEAVE)
                    .sender(uid)
                    .build());
        }
    }
}
