package ca.waterloo.dsg.graphflow.util;

import java.util.HashSet;
import ca.waterloo.dsg.graphflow.exceptions.NoDiffAvailableExceptoin;

public abstract class ObjectPool<T> {

    protected int maxSize;
    protected HashSet<T> used, available;
    protected abstract void reset(T object);

    private ObjectPool(){
        maxSize = 0;
        used = new HashSet<>(0);
        available = new HashSet<>(0);
    }

    protected ObjectPool(int size){
        maxSize = size;
        used = new HashSet<>(0);
        available = new HashSet<>(size);

        //System.out.println("The pool capacity is "+getCapacity() + " vs size="+size);
    }

    public int getCapacity() {return used.size() + available.size();}
    public int getAvailableSize() {return available.size();}
    public int getUsedSize() {return used.size();}

    public abstract void increaseSize(int size);
    public T getObject() throws IndexOutOfBoundsException{
        /*
        if (available.isEmpty()){
            increaseSize(maxSize);
            //throw new NoDiffAvailableExceptoin("There is no more Diffs available in the pool. Used size is "+getUsedSize()+ " while the capacity is "+ getCapacity());
        }
*/

        if(!available.iterator().hasNext()){
            System.out.println("No NEXT available, increase size from "+getCapacity()+ " by "+ maxSize);
            increaseSize(maxSize);

            System.out.println("The size now is "+getCapacity()+ " and checking if there is NEXT is "+ available.iterator().hasNext());
        }


        T object = available.iterator().next();
        available.remove(object);
        used.add(object);

        return object;
    }

    public void returnObject(T object){

        //System.out.println(" Returning+1 - used="+used.size());
        reset(object);
        boolean check = used.remove(object);
        if(!check)
            System.out.println("****************** ERROR, object does not exist in USED : "+ object);

        available.add(object);
        //System.out.println(" Returning+2 - used="+used.size());
    }

}
