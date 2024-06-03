package org.PS1;

public class Entity
{
    private User entity;

    public Entity(User entity) {
        this.entity = entity;
    }

    public User getEntity() {
        return entity;
    }

    public void setEntity(User entity) {
        this.entity = entity;
    }
}
