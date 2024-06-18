package org.PS1;

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
