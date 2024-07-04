package org.emp;

import org.json.JSONException;
import org.json.JSONObject;

import com.softwaretree.jdx.JDX_JSONObject;

/**
 * A shell (container) class defining a domain model object class for Inventory objects
 * based on the class JSONObject.  This class needs to define just two constructors.
 * Most of the processing is handled by the superclass JDX_JSONObject.
 * Description of the Inventory type object : 
 *    itemID-String primary key.
 *    itemName- Name of the item.
 *    quantity- Number of items in Inventory for a particular itemID.
 *    dob- long value to describe date in terms of milliseconds since 1/1/1970.
 */

public class JSON_InventoryItem extends JDX_JSONObject {
    public JSON_InventoryItem() {
        super();
    }
    public JSON_InventoryItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}
