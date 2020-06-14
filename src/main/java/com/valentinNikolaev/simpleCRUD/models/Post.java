package com.valentinNikolaev.simpleCRUD.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post implements Comparable<Post> {
    private long          id;
    private long          userId;
    private String        content;
    private LocalDateTime created;
    private LocalDateTime updated;

    private static long lastPostId = 0;

    public Post(Long userId, String content) {
        this.id      = ++ lastPostId;
        this.userId  = userId;
        this.content = content;
        this.created = LocalDateTime.now();
        this.updated = this.created;
    }

    public Post(long id, Long userId, String content, LocalDateTime created,
                LocalDateTime updated) {
        this.id      = id;
        this.userId  = userId;
        this.content = content;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatingDateAndTime() {
        return created;
    }

    public LocalDateTime getUpdatingDateAndTime() {
        return updated;
    }

    public void setContent(String content) {
        this.content = content;
        this.updated = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        int hash = this.content.hashCode() + (int) userId + created.hashCode() + updated.hashCode();
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
                this.userId == comparingObj.getUserId() && this.created.equals(
                comparingObj.getCreatingDateAndTime()) && this.updated.equals(
                comparingObj.getUpdatingDateAndTime());
    }

    public boolean equalsContent(Post post) {
        return this.content.equals(post.getContent());
    }

    @Override
    public int compareTo(Post post) {
        return this.created.compareTo(post.getCreatingDateAndTime());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter    = DateTimeFormatter.ofPattern("dd MM yyyy hh:mm a");
        String            creationTime = this.created.format(formatter);
        String            updatingTime = this.updated.format(formatter);

        String post =
                "\tPost id: " + this.id + "\tUser id: " + this.userId + "\n\tDate of creation: " +
                        creationTime + "\n\tDate of last updating: " + updatingTime + "\n\tPost" +
                        ":\n\t\t" +
                        this.content;


        return post;
    }


    public static void main(String[] args) {
        long userId = 6545;
        Post post = new Post(userId, "This is the text for checking out how is formatter " +
                "work.");

        System.out.println(post.toString());

    }


}
