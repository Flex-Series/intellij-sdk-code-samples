package org.intellij.sdk.toolWindow;

public class User {
    private String username;
    private int id;

    public User(String username, int id) {
        this.username = username;
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }


}
