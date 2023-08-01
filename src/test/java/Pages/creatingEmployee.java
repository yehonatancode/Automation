package Pages;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.Assert;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class creatingEmployee {

        public static final String URL = "https://dummy.restapiexample.com";
        public static final String create_endpoint = "https://dummy.restapiexample.com/api/v1/create";
    public static final String EMPLOYEE_NAME = "Johnny Adams";
    public static final String EMPLOYEE_SALARY = "15000";
    public static final String EMPLOYEE_AGE = "40";


    @Test
        public static void createEmployee(){
            JSONObject empDetails = new JSONObject();
            empDetails.put("name", EMPLOYEE_NAME);
            empDetails.put("salary", EMPLOYEE_SALARY);
            empDetails.put("age", EMPLOYEE_AGE);


            Response response = given()
                    .contentType("application/json")
                    .body(empDetails.toString())
                    .when()
                    .post(create_endpoint);


            response.then()
                    .statusCode(200);




            response.then()
                    .body("data.id", notNullValue())
                    .body("data.name", equalTo(EMPLOYEE_NAME))
                    .body("data.salary", equalTo(EMPLOYEE_SALARY))
                    .body("data.age", equalTo(EMPLOYEE_AGE));


            response.then()
                    .body("status", equalTo("success"));




            response.then()
                    .body("data.id", instanceOf(Integer.class));
            response.prettyPrint();




        }
        public static void main(String[] args) {
            createEmployee();
        }


    }

