package cn.edu.xmu.chat.demos.web.handler;

import cn.edu.xmu.chat.demos.web.service.GroupService;
import cn.edu.xmu.chat.demos.web.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class MessageReceiver {
    private final UserService userService;
    private final GroupService groupService;
    private final ObjectMapper mapper = new ObjectMapper();

    public MessageReceiver(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @JmsListener(destination = "privateChatQueue")
    public void receivePrivateMessage(String jsonMessage) {
        try {
            Map<String, String> map = mapper.readValue(jsonMessage, Map.class);
            String senderId = map.get("sender");
            String receiverId = map.get("receiver");
            String content = map.get("content");
            userService.sendMessageToUser(receiverId, "私信 来自 " + senderId + ": " + content);
            userService.sendMessageToUser(senderId, "你 对 " + receiverId + ": " + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "groupChatQueue")
    public void receiveGroupMessage(String jsonMessage) {
        try {
            Map<String, String> map = mapper.readValue(jsonMessage, Map.class);
            String senderId = map.get("sender");
            String groupName = map.get("group");
            String content = map.get("content");
            List<String> memberIds = groupService.getGroupMembers(groupName);
            if (memberIds != null) {
                for (String memberId : memberIds) {
                    userService.sendMessageToUser(memberId, "群聊 " + groupName + " 来自 " + senderId + ": " + content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}