package org.emp;

import org.json.JSONException;
import org.json.JSONObject;

import com.softwaretree.jdx.JDX_JSONObject;

public class JSON_Sale extends JDX_JSONObject {
    public JSON_Sale()
    {
        super();
    }
    public JSON_Sale(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}
