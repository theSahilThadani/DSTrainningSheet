package Day6_7.managers;

import Day6_7.datastructures.Trie;
import Day6_7.entities.User;
import Day6_7.entities.UserInfo;
import Day6_7.entities.UserStatus;
import Day6_7.utils.IdGenerator;
import Day6_7.utils.PasswordHasher;

import java.util.*;

public class UserManager {

    private final HashMap<String, User> usersByUsername;
    private final HashMap<String, User> usersById;
    private final Trie<User> usernameTrie;

    private final EnumMap<UserStatus, Set<String>> userIdsByStatus;



    public UserManager() {
        this.usersByUsername = new HashMap<>();
        this.usersById = new HashMap<>();
        this.userIdsByStatus = new EnumMap<>(UserStatus.class);
        this.usernameTrie = new Trie<>();
        //initialize the enumMap to map each status with set of userIds
        for (UserStatus status : UserStatus.values()) {
            userIdsByStatus.put(status, new HashSet<>());
        }

    }

    //register the user
    public synchronized User registerUser(String username, String password, String displayName) {
        // Validation for username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        //pass validation
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Normalize username
        username = username.toLowerCase().trim();

        // Check if username already exists
        if (usersByUsername.containsKey(username)) {
            System.out.println(" Username already taken: " + username);
            return null;
        }

        // Generate unique userId
        String userId = IdGenerator.generateUserId();

        // Hashing the password
        String passwordHash = PasswordHasher.hash(password);

        // Creating the user
        User newUser = new User(userId, username, displayName, passwordHash);

        // Add to indexes
        usersByUsername.put(username, newUser);
        usersById.put(userId, newUser);
        usernameTrie.insert(username, newUser);
        // Add to status tracking (initially OFFLINE)
        userIdsByStatus.get(UserStatus.OFFLINE).add(userId);


        System.out.println("User is registered: " + newUser.getUsername());
        return newUser;
    }

    //login the user
    public synchronized User loginUser(String username, String password) {
        // Validation
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        username = username.toLowerCase().trim();

        // check the user
        User user = usersByUsername.get(username);
        if (user == null) {
            System.out.println("User not found: " + username);
            return null;
        }

        // Verify password
        String passwordHash = PasswordHasher.hash(password);
        if (!user.verifyPassword(passwordHash)) {
            System.out.println("Invalid password for: " + username);
            return null;
        }

        // Update status to ONLINE for current session
        updateUserStatus(user, UserStatus.ONLINE);

        System.out.println("User logged in: " + user.getUsername());
        return user;
    }


    public synchronized void logoutUser(User user) {
        if (user == null) return;

        updateUserStatus(user, UserStatus.OFFLINE);
        System.out.println("User logged out: " + user.getUsername());
    }


    public synchronized void updateUserStatus(User user, UserStatus newStatus) {
        if (user == null || newStatus == null) {
            throw new IllegalArgumentException("User and status cannot be null");
        }

        UserStatus oldStatus = user.getStatus();


        user.updateStatus(newStatus);

        //here removing the old status of user from set.
        userIdsByStatus.get(oldStatus).remove(user.getUserId());

        // Add to new status set
        userIdsByStatus.get(newStatus).add(user.getUserId());

        System.out.println(String.format("%s: %s → %s",
                user.getUsername(), oldStatus, newStatus));
    }


    public User getUserByUsername(String username) {
        if (username == null) return null;
        return usersByUsername.get(username.toLowerCase().trim());
    }


    public User getUserById(String userId) {
        if (userId == null) return null;
        return usersById.get(userId);
    }

    //get all user with same status TC = O(K) K is number of users in set
    public List<UserInfo> getUsersByStatus(UserStatus status) {
        Set<String> userIds = userIdsByStatus.get(status);
        List<UserInfo> users = new ArrayList<>(userIds.size());

        for (String userId : userIds) {
            User user = usersById.get(userId);
            if (user != null) {
                users.add(user.toUserInfo());
            }
        }

        return users;
    }


    public int getOnlineUserCount() {
        return userIdsByStatus.get(UserStatus.ONLINE).size();
    }


    // here we are using tries to search user by username o(m+k) m =  search length, k = results
    public List<UserInfo> searchUsersByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }

        prefix = prefix.toLowerCase().trim();


        List<User> matchedUsers = usernameTrie.searchByPrefix(prefix);

         // Convert to UserInfo
        List<UserInfo> results = new ArrayList<>(matchedUsers.size());
        for (User user : matchedUsers) {
            results.add(user.toUserInfo());
        }

        // Sort by username for consistent results
        results.sort(Comparator.comparing(UserInfo::getUsername));

        return results;
    }

    //auto complete using tries
    public List<UserInfo> autocompleteUsername(String prefix, int limit) {
        List<UserInfo> allResults = searchUsersByPrefix(prefix);

        // Return only top N results
        return allResults.subList(0, Math.min(limit, allResults.size()));
    }

    //check username if available
    public boolean isUsernameAvailable(String username) {
        if (username == null) return false;
        return !usersByUsername.containsKey(username.toLowerCase().trim());
    }

    public int getTotalUserCount() {
        return usersById.size();
    }
}
