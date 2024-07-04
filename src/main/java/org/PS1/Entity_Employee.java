package org.PS1;

public class Entity_Employee
{
    /* This class has been created to get the JSON object that we are sending to the Gilahri microservice into the
     right format which is, {"entity":{"id":1, "name":"xyz","exempt":false,"compensation":1234.50,"dob":10022024}}
     */
    private Employee entity;

    public Entity_Employee(Employee entity) {
        this.entity = entity;
    }

    public Employee getEntity() {
        return entity;
    }

    public void setEntity(Employee entity) {
        this.entity = entity;
    }
}
