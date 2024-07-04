package org.PS1;
// This class exists to provide the correct format for the POST request to Gilhari. The format is: {"entity":{...}}
public class Entity_Shipment
{
    Shipment entity;
    public Entity_Shipment(Shipment entity)
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
