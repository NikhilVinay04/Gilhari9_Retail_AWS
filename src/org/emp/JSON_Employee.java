package org.emp;
import org.json.JSONException;
import org.json.JSONObject;
import com.softwaretree.jdx.JDX_JSONObject;
/**
 * A shell (container) class defining a domain model object class for Employee objects
 * based on the class JSONObject.  This class needs to define just two constructors.
 * Most of the processing is handled by the superclass JDX_JSONObject.
 * Description of the Employee type object : 
 *    id-String primary key 
 *    name- Name of the Employee
 *    exempt- Boolean value to check if the Employee is exempt or not.
 *    compensation- Salary of the Employee.
 *    dob- long value to describe date of birth in terms of milliseconds since 1/1/1970.
 */
public class JSON_Employee extends JDX_JSONObject {

    public JSON_Employee() {
        super();
    }

    public JSON_Employee(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}
