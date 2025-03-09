package cn.edu.xmu.chat.demos.web;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroupService {
    private final Map<String, List<String>> groups = new ConcurrentHashMap<>();

    public void createGroup(String groupName) {
        groups.putIfAbsent(groupName, new ArrayList<>());
    }

    public void joinGroup(String groupName, String userId) {
        List<String> members = groups.get(groupName);
        if (members != null && !members.contains(userId)) {
            members.add(userId);
        }
    }

    public List<String> getGroupMembers(String groupName) {
        return groups.get(groupName) != null ? new ArrayList<>(groups.get(groupName)) : null;
    }

    public List<String> getAllGroups() {
        return new ArrayList<>(groups.keySet());
    }
}