package org.emp;
import org.json.JSONException;
import org.json.JSONObject;

import com.softwaretree.jdx.JDX_JSONObject;
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
