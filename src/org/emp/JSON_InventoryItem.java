package org.emp;

import org.json.JSONException;
import org.json.JSONObject;

import com.softwaretree.jdx.JDX_JSONObject;

public class JSON_InventoryItem extends JDX_JSONObject {
    public JSON_InventoryItem() {
        super();
    }
    public JSON_InventoryItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}
