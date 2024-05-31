package org.smartregister.chw.util;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;

public class JSONFilterTest {
    @Test
    public void testJSONFilter() {
        try {
            String filename = "";
            File file = new File("/home/n2/.projects/ucs/opensrp-client-chw/opensrp-chw/src/main/assets/ec_client_fields.json");
            JsonQ jFilter = new JsonQ(file);
            Object a = jFilter.get("bindFields[@.name=ec_referral");
            System.out.println(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test void testJaribuhapa(){
        int a= 2 + 4;
        int b= 4 - 2;
        assertEquals(a,b);
    }
}
