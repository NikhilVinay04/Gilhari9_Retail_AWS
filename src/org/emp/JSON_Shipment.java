package org.emp;
import org.json.JSONException;
import org.json.JSONObject;

import com.softwaretree.jdx.JDX_JSONObject;

/**
 * A shell (container) class defining a domain model object class for Shipment objects
 * based on the class JSONObject.  This class needs to define just two constructors.
 * Most of the processing is handled by the superclass JDX_JSONObject.
 * Description of the Sales type object : 
 *    id-String primary key.
 *    itemID- ID of the Inventory item.
 *    itemName- Name of the item.
 *    quantity- Number of items being shipped for a particular itemID.
 *    date- long value to describe date in terms of milliseconds since 1/1/1970.
 */

public class JSON_Shipment extends JDX_JSONObject
{
    public JSON_Shipment()
    {
        super();
    }
    public JSON_Shipment(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}
