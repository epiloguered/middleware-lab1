package cn.edu.xmu.chat.demos.web.controller;

import cn.edu.xmu.chat.demos.web.service.GroupService;
import cn.edu.xmu.chat.demos.web.handler.MessageSender;
import cn.edu.xmu.chat.demos.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final MessageSender messageSender;

    @Autowired
    private UserService userService;

    private GroupService groupService;

    public UserController(MessageSender messageSender, UserService userService, GroupService groupService) {
        this.messageSender = messageSender;
        this.userService = userService;
        this.groupService = groupService;
    }
    @GetMapping("/onlineUsers")
    public List<String> getOnlineUsers() {
        return userService.getOnlineUsers();
    }
    @PostMapping("/createGroup")
    public String createGroup(@RequestParam String groupName) {
        groupService.createGroup(groupName);
        return "群聊 " + groupName + " 已创建!";
    }

    @PostMapping("/joinGroup")
    public String joinGroup(@RequestParam String groupName, @RequestParam String userId) {
        groupService.joinGroup(groupName, userId);
        return "用户 " + userId + " 已加入群聊 " + groupName;
    }

    @GetMapping("/groupList")
    public List<String> getGroupList() {
        return groupService.getAllGroups();
    }
    @PostMapping("/login")
    public String login(@RequestParam String userId) {
        return "用户 " + userId + " 已登录!";
    }

    @PostMapping("/privateChat")
    public String sendPrivateMessage(@RequestParam String senderId, @RequestParam String receiverId, @RequestParam String message) {
        String jsonMessage = String.format("{\"sender\":\"%s\", \"receiver\":\"%s\", \"content\":\"%s\"}", senderId, receiverId, message);
        messageSender.sendMessage("privateChatQueue", jsonMessage);
        return "私信已发送!";
    }

    @PostMapping("/groupChat")
    public String sendGroupMessage(@RequestParam String senderId, @RequestParam String groupName, @RequestParam String message) {
        String jsonMessage = String.format("{\"sender\":\"%s\", \"group\":\"%s\", \"content\":\"%s\"}", senderId, groupName, message);
        messageSender.sendMessage("groupChatQueue", jsonMessage);
        return "群消息已发送!";
    }

    @GetMapping("/messageHistory")
    public List<String> getMessageHistory(@RequestParam String type, @RequestParam String id) {
        String fileName = type.equals("private") ? "private_" + id + ".txt" : "group_" + id + ".txt";
        List<String> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}