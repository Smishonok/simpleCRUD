package com.valentinNikolaev.simpleCRUD.models;

import java.time.LocalDateTime;

public class Post implements Comparable<Post> {
    private long          id;
    private User          user;
    private String        content;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Post(long postId, User user, String content) {
        this.id      = postId;
        this.user    = user;
        this.content = content;
        this.created = LocalDateTime.now();
        this.updated = this.created;
    }

    public Post(long id, User user, String content, LocalDateTime created, LocalDateTime updated) {
        this.id      = id;
        this.user    = user;
        this.content = content;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateOfCreation() {
        return created;
    }

    public LocalDateTime getDateOfLastUpdate() {
        return updated;
    }

    public void setContent(String content) {
        this.content = content;
        this.updated = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        int hash = this.content.hashCode() + user.hashCode() + created.hashCode() + updated.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.hashCode() != obj.hashCode()) {
            return false;
        }

        Post comparingObj = (Post) obj;
        return this.content.equals(comparingObj.content) &&
                this.user.equals(comparingObj) && this.created.equals(
                comparingObj.getDateOfCreation()) && this.updated.equals(
                comparingObj.getDateOfLastUpdate());
    }

    public boolean equalsContent(Post post) {
        return this.content.equals(post.getContent());
    }

    @Override
    public int compareTo(Post post) {
        return this.created.compareTo(post.getDateOfCreation());
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", user=" + user + ", content='" + content + '\'' +
                ", created=" + created + ", updated=" + updated + '}';
    }
}
