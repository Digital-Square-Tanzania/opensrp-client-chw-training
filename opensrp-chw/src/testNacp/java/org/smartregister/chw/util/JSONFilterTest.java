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
            File file = new File("/home/n2/.projects/ucs/opensrp-client-chw/opensrp-chw/src/testNacp/java/org/smartregister/chw/util/" + filename);
            String[] paths = {
                    "store...price",
                    "store.book",
                    "store.book.1.price",
                    "store.book[*].range[*].pages",
                    "store..range[(@.color==red)].pages",
                    "store.book[(@.price != null )&(@.category!=fiction)]",
            };

            JsonQ jFilter = new JsonQ(file);
            Object a = jFilter.get("store.book");
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

class Range{
    String pages;
    String color;
    Range(JSONObject obj){
        pages=obj.optString("pages","");
        color=obj.optString("color","");
    }
}