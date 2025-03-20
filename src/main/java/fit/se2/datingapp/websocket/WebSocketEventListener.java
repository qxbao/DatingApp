package fit.se2.datingapp.websocket;

import fit.se2.datingapp.service.MatchingService;
import fit.se2.datingapp.service.UserUtilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageOperations;
    private final MatchingService matchingService;
    private final UserUtilityService userUtilityService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Long uid = (Long) accessor.getSessionAttributes().get("uid");
        if  (uid != null) {
            log.info("User Disconnected: {} ", uid);
            List<String> matchedUsers = matchingService.getMatches(userUtilityService.getUserById(uid)).stream().map(
                match -> Objects.equals(match.getUser1().getId(), uid) ? match.getUser2().getEmail() : match.getUser1().getEmail()
            ).toList();
            SocketMessage socketMessage = SocketMessage.builder()
                    .type(SocketMessage.MessageType.LEAVE)
                    .sender(uid)
                    .build();
            for (String email : matchedUsers) {
                messageOperations.convertAndSendToUser(
                        email,
                        "/queue/chat",
                        socketMessage
                );
            }
        }
    }
}
