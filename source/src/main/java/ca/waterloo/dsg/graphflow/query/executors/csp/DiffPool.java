package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.ObjectPool;

class Diff{
    protected short iterationNo;

    protected Diff(short iterationNo){
        this.iterationNo = iterationNo;
    }

    public String toString(){
        String str = "["+iterationNo+"]";
        return str;
    }

    public void initialize(short s){
        iterationNo = s;
    }

    public void reset(){
        iterationNo = 0;
    }
}


public class DiffPool extends ObjectPool<Diff> {

    private static DiffPool instance = null;
    private static int poolSize;

    @Override
    protected void reset(Diff object) {
        object.reset();
    }

    private DiffPool(int size){
        super(size);

        for(int i=0;i<size;i++)
            available.add(new Diff((short) 0));

        poolSize = size;
    }

    public static DiffPool createPool(int size) {
        if (instance == null)
        {
            synchronized(ObjectPool.class){
                if (instance == null){
                    instance = new DiffPool(size);
                    return instance;
                }
            }
        }

        if(size > poolSize){
            for(int i=0;i<(size-poolSize);i++)
                instance.available.add(new Diff((short) 0));

            poolSize =size;
            instance.maxSize = size;
        }
        return instance;
    }

    public Diff getObject(short i) throws IndexOutOfBoundsException {
        Diff object = getObject();
        object.initialize(i);
        return object;
    }

    public void increaseSize(int size){
        for(int i=0;i<size;i++)
            available.add(new Diff((short) 0));
    }
}




