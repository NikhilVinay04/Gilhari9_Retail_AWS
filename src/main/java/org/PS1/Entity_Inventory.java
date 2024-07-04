package org.PS1;
// This class exists to provide the correct format for the POST request to Gilhari. The format is: {"entity":{...}}
public class Entity_Inventory
{
    Inventory entity;
    public Entity_Inventory(Inventory entity)
    {
        this.entity = entity;
    }

    public Inventory getEntity() {
        return entity;
    }

    public void setEntity(Inventory entity) {
        this.entity = entity;
    }
}
