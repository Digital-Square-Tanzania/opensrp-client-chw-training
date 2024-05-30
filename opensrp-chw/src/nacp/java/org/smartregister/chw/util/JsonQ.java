package org.smartregister.chw.util;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

/** @noinspection unused*/
public class JsonQ {
    private Object root;
    public JsonQ(String json){root=getRoot(json);}
    public JsonQ(InputStream jsonStream){
        root=getRoot(stringFromIO(jsonStream));
    }
    public JsonQ(File jsonFile){
        root=getRoot(stringFromIO(jsonFile));
    }
    public JsonQ(List<?> jsonArrays){root=jsonArrays;}
    public JsonQ(JSONObject json){root=json;}
    @SuppressLint("LogNotTimber")
    private void log(Exception e){
        Log.e(this.getClass().getSimpleName(),"",e);
    }


    private String stringFromIO(Object input){
        try{
            BufferedReader reader;
            if(input instanceof File)
                reader=new BufferedReader(new FileReader((File)input));
            else if (input instanceof InputStream)
                reader = new BufferedReader(new InputStreamReader((InputStream) input));
            else return "";

            StringBuilder sb= new StringBuilder();
            for(String s=reader.readLine();s!=null;s=reader.readLine()) sb.append(s);
            return sb.toString();
        } catch (IOException e){log(e);}
        return  "";
    }
    private Object getRoot(String jsonRoot){
        try{
            try {root=new JSONObject(jsonRoot);}
            catch (JSONException e) {root = new JSONArray(jsonRoot);}}
        catch (JSONException e){log(e);}
        return new Object();
    }

    private  <T> List<T> getListImpl(String jsonPath, JFunction<Object,T> changer){
        List<T> list=new ArrayList<>();
        flatForEach(get(jsonPath),(key,obj)-> list.add(changer.apply(obj)));
        return list;
    }

    public  <T> List<T> getList(String jsonPath, JFunction<JSONObject,T> changer){
        List<T> list=new ArrayList<>();
        flatForEach(get(jsonPath),(key,obj)->{
            if(obj instanceof JSONObject)
                list.add(changer.apply((JSONObject) obj));
        });
        return list;
    }

    public  <T> List<T> listFromJSONArrays(String jsonPath, JFunction<JSONArray,T> changer){
        List<T> list=new ArrayList<>();
        flatForEach(get(jsonPath),(key,obj)->{
            if(obj instanceof JSONArray)
                list.add(changer.apply((JSONArray) obj));
        });
        return list;
    }

    public  List<Double> getDoubles(String jsonPath){
        return getListImpl(jsonPath,Double.class::cast);
    }

    public  List<Float> getFloats(String jsonPath){
        return getListImpl(jsonPath,obj->(float)( (double)obj));
    }

    public  List<Integer> getInts(String jsonPath){
        return getListImpl(jsonPath, obj->((int)Math.round((Double)obj)));
    }

    public  List<String> getStrings(String jsonPath){
        return getList(jsonPath, String.class::cast);
    }


    public  <T> T get(String jsonPath, Class<T> clazz){
        Object o=get(jsonPath);
        if( o instanceof List<?>){
            List<?> x=(List<?>)o;
            return x.isEmpty()?null:clazz.cast(x.get(0));
        }
        return clazz.cast(o);
    }

    public  <T> T get(String jsonPath, JFunction<Object,T> changer){
        return changer.apply(get(jsonPath));
    }

    public  Object get(String jsonPath) {
        List<Object> results = new ArrayList<>();
        List<String> paths = evaluatePath(jsonPath);
        results.add(root);

        List<Object> temp = new ArrayList<>();
        for (String path : paths) {
            temp.clear();
            Taker<Object> taker;
            if (path.matches(REGULAR_PATH))
                taker=(key,obj)->temp.add(handleNormalPath(path,obj));
            else if (path.matches(PATH_EXPRESSION))
                taker=(key,obj)->temp.addAll(filter(obj, path));
            else if (path.matches(WILDCARD))
                taker=(key,obj)->temp.addAll(findMatchingPath(path,obj));
            else if (path.matches(MATCH_ALL))
                taker=(key,obj)->listAndJsonArrayForEach(obj,(k,v)-> temp.add(v));
            else return null;
            listAndJsonArrayForEach(results,taker);
            results.clear();
            results.addAll(temp);
        }
        return results;
    }

    private  void listAndJsonArrayForEach(Object input, Taker<Object> consumer) {
        if (input instanceof Collection<?> || input instanceof JSONArray) {
            flatForEach(input, consumer);
        } else consumer.take("", input);
    }

    public  List<Object> findMatchingPath(String path, Object root) {
        List<Object> results = new ArrayList<>();
        Deque<Object> stack = new ArrayDeque<>();
        Set<Object> seen = new HashSet<>();
        stack.push(root);
        path=path.replaceAll("^\\W+","");
        while (!stack.isEmpty()) {
            Object current = stack.pop();
            Object res = handleNormalPath(path, current);
            if (res != null) {
                results.add(res);
            }
            flatForEach(current, (key, obj) -> {
                if (obj == null || seen.contains(obj)) return;
                stack.push(obj);
                seen.add(obj);
            });
        }
        return results;
    }

    private  List<String> evaluatePath(String jsonPath) {
        Matcher parts=Pattern.compile(PATH_POSSIBILITIES).matcher(jsonPath);
        List<String> paths = new ArrayList<>();
        while (parts.find()) {
            paths.add(parts.group(1));
            if (parts.group(2) != null) paths.add(parts.group(2));
            if (parts.group(3) != null) paths.add(parts.group(3));
        }
        return paths;
    }

    private  boolean jsonPrimitive(Object o){
        return o instanceof Number || o instanceof String || o instanceof  Boolean;
    }
    private  final BoolEvaluator BOOL=new BoolEvaluator();
    private  List<Object> filter(Object object, String expression) {
        List<Object> results=new ArrayList<>();
        listAndJsonArrayForEach(object, (key, obj) -> {
            boolean evaluation=false;
            if(obj instanceof  JSONObject) {
                JSONObject json=(JSONObject)obj;
                Matcher m=JSON_VARIABLE.matcher(expression);
                String exp=expression;
                while(m.find()){
                    String variable = m.group(1);
                    Object val=json.opt(variable); if(val==null) return;
                    exp = exp.replace("@." + variable, String.valueOf(val));
                }
                evaluation = BOOL.evaluate(exp);
            }
            else if (jsonPrimitive(obj)){
                evaluation = BOOL.evaluate(expression.replace("@." + key, String.valueOf(obj)));
            }
            if(evaluation)results.add(obj);
        });
        return results;
    }

    private  Object handleNormalPath(String path, Object object) {
        if (path == null || path.isEmpty()) return object;
        Object current = object;
        for (String p : path.split("\\.")) current = jsonValue(p, current);
        return current == object ? null : current;
    }

    private  Object jsonValue(String key, Object jsonThing) {
        if (jsonThing instanceof JSONObject) {
            return ((JSONObject) jsonThing).opt(key);
        } else if (jsonThing instanceof JSONArray) {
            try { return ((JSONArray) jsonThing).opt(Integer.parseInt(key));}
            catch (NumberFormatException e) {
                JSONException jsonException = new JSONException(String.format("Invalid index %s for JSONArray", key));
                log(jsonException);
                return null;
            }
        }
        return jsonThing;
    }


    public  <T> T val(String key, Object jsonThing, Class<T> clazz) {
        Object value = jsonThing instanceof Map<?, ?> ? ((Map<?, ?>) jsonThing).get(Integer.parseInt(key))
                : jsonValue(key, jsonThing);

        if (clazz.isInstance(value)) return clazz.cast(value);
        else
            throw new ClassCastException("Cannot cast the value to the specified type: " + clazz.getName());
    }

    private  void flatForEach(Object input, Taker<Object> consumer) {
        if (input instanceof JSONArray) {
            JSONArray arr = (JSONArray) input;
            for (int x = 0, l = arr.length(); x < l; x++) {
                Object prop = arr.opt(x);
                if (prop != null) consumer.take(String.valueOf(x), prop);
            }
        } else if (input instanceof JSONObject) {
            JSONObject obj = (JSONObject) input;
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object prop = obj.opt(key);
                if (prop != null) consumer.take(key, prop);
            }
        } else if (input instanceof Map<?, ?>) {
            Map<?, ?> data = (Map<?, ?>) input;
            for (Map.Entry<?, ?> entry : data.entrySet()) {
                Object prop = entry.getValue();
                if (prop != null) consumer.take(entry.getKey().toString(), prop);
            }
        } else if (input instanceof Collection<?>) {
            Collection<?> data = (Collection<?>) input;
            for (Object obj : data) {
                if (obj != null) consumer.take("", obj);
            }
        } else consumer.take("", input);
    }

    private interface Taker<T> { void take(String key, T t);}
    public interface JFunction<S,T> { T apply(S s);}

    private static final String REGULAR_PATH ="\\w+(?:\\.\\w+)*";
    private static final String MATCH_ALL="\\[\\*]";
    private static final String WILDCARD="\\.{2,3}"+REGULAR_PATH;
    private static final String PATH_EXPRESSION=".*\\[\\??\\(.*";
    private static final String PATH_POSSIBILITIES="("+REGULAR_PATH+")("+WILDCARD+")?(\\([^)]+\\)|\\[[^]]+])?";
    private static final Pattern JSON_VARIABLE=Pattern.compile("@\\.(\\w+)");
}