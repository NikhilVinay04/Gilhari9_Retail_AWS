package org.PS1;

public class Sales
{
    int id;
    int itemID;
    String itemName;
    double quantity;
    long date;
    public Sales(int id, int itemID, String itemName, double quantity, long date)
    {
        this.id = id;
        this.itemID = itemID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getItemID() {
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
