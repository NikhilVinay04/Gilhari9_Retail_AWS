package org.PS1;

public class Entity
{
    /* This class has been created to get the JSON object that we are sending to the Gilahri microservice into the
     right format which is, {"entity":{"id":1, "name":"xyz","exempt":false,"compensation":1234.50,"dob":10022024}}
     */
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
