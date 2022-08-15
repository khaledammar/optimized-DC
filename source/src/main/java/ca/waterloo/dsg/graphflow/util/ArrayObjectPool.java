package ca.waterloo.dsg.graphflow.util;

import java.util.HashSet;

public abstract class ArrayObjectPool<T> {

    protected T[] buffer;
    protected int position;
    protected int increaseRate;
    protected abstract void reset(T object);

    private ArrayObjectPool(){
        position = 0;
    }

    protected ArrayObjectPool(int size){
        position = 0;
        increaseRate = size;
    }

    public int getCapacity() {return buffer.length;}
    public int getUsedSize() {return position;}

    public abstract void increaseSize(int size);
    public T getObject() throws IndexOutOfBoundsException{

        if(position == buffer.length - 1){
            System.out.println("No NEXT available, increase size from "+getCapacity()+ " by "+ increaseRate);
            increaseSize(increaseRate);
        }

        T object = buffer[position];
        buffer[position] = null;
        position++;

        return object;
    }

    public void returnObject(T object){

        position--;
        buffer[position] = object;
    }

}
