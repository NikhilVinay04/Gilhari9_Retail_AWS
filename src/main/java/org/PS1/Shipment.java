package org.PS1;
//This class represents the schema of the Shipment table
public class Shipment
{
    String id;
    String itemID;
    String itemName;
    double quantity;
    long date;
    public Shipment(String id, String itemID, String itemName, double quantity, long date)
    {
        this.id = id;
        this.itemID = itemID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getItemID() {
        return itemID;
    }

    public double getQuantity() {
        return quantity;
    }

    public long getDate() {
        return date;
    }

    public String getItemName() {
        return itemName;
    }
}

