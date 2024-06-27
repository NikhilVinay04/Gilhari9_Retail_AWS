package org.PS1;
// This class exists to provide the correct format for the POST request to Gilhari. The format is: {"entity":{...}}
public class Entity_Ship
{
    Shipment entity;
    public Entity_Ship(Shipment entity)
    {
        this.entity = entity;
    }

    public Shipment getEntity() {
        return entity;
    }

    public void setEntity(Shipment entity) {
        this.entity = entity;
    }
}
