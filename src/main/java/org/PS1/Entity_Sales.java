package org.PS1;
// This class exists to provide the correct format for the POST request to Gilhari. The format is: {"entity":{...}}
public class Entity_Sales
{
    Sales entity;
    public Entity_Sales(Sales entity)
    {
        this.entity = entity;
    }

    public Sales getEntity() {
        return entity;
    }

    public void setEntity(Sales entity) {
        this.entity = entity;
    }
}
