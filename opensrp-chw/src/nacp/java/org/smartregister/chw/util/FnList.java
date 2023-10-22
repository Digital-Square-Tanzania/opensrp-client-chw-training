package org.smartregister.chw.util;

import static org.smartregister.chw.util.FnInterfaces.Accumulator;
import static org.smartregister.chw.util.FnInterfaces.Action;
import static org.smartregister.chw.util.FnInterfaces.Function;
import static org.smartregister.chw.util.FnInterfaces.Producer;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import timber.log.Timber;



/**
 * Class FnList provides functional operations on a list of items.
 * <p>
 * WARNING: This class is designed to swallow exceptions occurring during its operations.
 * Any exceptions thrown during operations such as map, filter, etc. will be caught
 * and will not propagate to the calling code. Instead, all errors are logged using Timber.e.
 * <p>
 * This behavior ensures smooth operation of the pipeline but can potentially hide issues
 * or unexpected behaviors. When debugging or troubleshooting, be sure to check the logs
 * for any relevant error messages from this class.
 * <p>
 * It's important to be aware of this when using this class, especially if
 * operations have side effects or if there's a need to know about any failures.
 */
@SuppressWarnings({"unchecked","unused"})
public class FnList<T> implements Iterable<T>{

    private final Iterable<T> source;
    private final List<Function<T,T>> operations = new ArrayList<>();

    public FnList(T[] data) {
        source= new ArrayList<>(Arrays.asList(data));
    }
    public FnList(Collection<T> collection) {
        source=new ArrayList<>(collection);
    }
    private FnList(Iterable<T> source,List<Function<T,T>> operations){
        this.source=source; this.operations.addAll(operations);
    }

    public static FnList<Integer> range(int lower, int upper){
        List<Integer> numbers=new ArrayList<>();
        for(int i=lower;i<upper;i++){
            numbers.add(i);
        }
        return new FnList<>(numbers);
    }

    public static FnList<Integer> range(int upper){
        return FnList.range(0,upper);
    }

    public FnList<T> filter(FnInterfaces.Predicate<T> predicate) {
        List<Function<T,T>> ops = new ArrayList<>(this.operations);
        ops.add(input -> predicate.test(input) ? input : null);
        return new FnList<>(source,ops);
    }

    public <S> FnList<S> map(Function<T, S> function) {
        List<Function<T,T>> ops = new ArrayList<>(this.operations);
        ops.add(input -> (T) function.invoke(input));
        // Cast is safe here because we're just transforming data.
        return (FnList<S>) new FnList<>(source,ops);
    }

    public <A> A reduce(A identity,Accumulator<A, T> accumulator) {
        A result=identity;
        for (T item : this) {
            try{ result = accumulator.combine(result, item);}
            catch (Exception e){ Timber.e(e);}
        }
        return result;
    }

    public FnList<T> unique(){ return unique(x->x); }

    public <S> FnList<T> unique(Function<T, S> giveId){
        Set<S> ids=new LinkedHashSet<>();ids.add(null);
        return this.filter(x->ids.add(ex(()->giveId.invoke(x))));
    }

    public List<T> list(){
        return reduce(new ArrayList<>(),(a,b)->{a.add(b);return a;});
    }

    public Set<T> toSet(){
        return new LinkedHashSet<>(unique().list());
    }

    @NonNull
    public String toString(){
        return source.toString();
    }

    public String toJson(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        return gson.toJson(source);
    }

    public void forEachItem(Action<T> action) {
        for(T t:this){
            ex(()->action.apply(t));
        }
    }

    @NonNull @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
    private  static <T> T ex(Producer<T> producer){
        try { return producer.produce();}
        catch (Exception e) {Timber.e(e);}
        return null;
    }
    private  static void ex(FnInterfaces.Runnable runnable){
        ex(()->{runnable.run(); return null;});
    }

    public FnList<T> prune() {
        return filter(it -> it != null
                && (!(it instanceof String) || !((String) it).trim().isEmpty())
                && (!(it instanceof Collection) || !((Collection<?>) it).isEmpty())
                && (!(it instanceof Map) || !((Map<?, ?>) it).isEmpty())
                && (!(it instanceof CharSequence) || ((CharSequence) it).length() > 0)
                && ( !it.getClass().isArray() || java.lang.reflect.Array.getLength(it) > 0 )
        );
    }

    @NonNull @Override
    public Iterator<T> iterator() {
        return new FnListIterator();
    }


    private class FnListIterator implements Iterator<T> {
        private final Iterator<T> sourceIterator = source.iterator();
        private T nextValue;
        private boolean isEvaluated = false;

        @Override
        public boolean hasNext() {
            if (isEvaluated) return nextValue != null;

            while (sourceIterator.hasNext()) {
                nextValue = sourceIterator.next();
                for (Function<T, T> operation : operations) {
                    nextValue = ex(() -> operation.invoke(nextValue));
                    if (nextValue == null) break;
                }
                if (nextValue != null) {
                    isEvaluated = true;
                    return true;
                }
            }
            isEvaluated = true;
            return false;
        }

        @Override
        public T next() {
            if (!isEvaluated && !hasNext()) throw new NoSuchElementException();
            isEvaluated = false;
            T returnValue = nextValue;
            nextValue = null;
            return returnValue;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
