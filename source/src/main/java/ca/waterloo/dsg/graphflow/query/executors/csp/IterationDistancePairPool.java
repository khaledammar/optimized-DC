package ca.waterloo.dsg.graphflow.query.executors.csp;


import ca.waterloo.dsg.graphflow.util.ObjectPool;

/**
 * Represents an iteration number and a distance that a vertex took during
 * a particular iteration of the BFS.
 */
class IterationDistancePair extends Diff{
     double distance;

    /**
     * The memory requirement for Diff should be 2 (short), while the memory requirement for IterationDistancePair
     * should be 2+8 (short + double) - so IterationDistancePair should require 5X Diff.
     * However, due to the JVM requirement, each one has a 16 bytes used for Object - note that we are ignoring the
     * badding overhead here.
     *
     * So, in order to keep the correct 5X ration, we need to add 8 double dummy variables to represent the dummy 64 bytes.
     * The total memory for Diff is going to be: 16 + 2 = 18
     * The total memory for IterationDistancePair is going to be: 16 + 2 + 8 +(64 dummy) = 90
     *
     */
    private double dummy1;
    private double dummy2;
    private double dummy3;
    private double dummy4;
    private double dummy5;
    private double dummy6;
    private double dummy7;
    private double dummy8;


    protected IterationDistancePair(short iterationNo, double distance) {
        super(iterationNo);
        this.distance = distance;

        dummy1 = 0;
        dummy2 = 0;
        dummy3 = 0;
        dummy4 = 0;
        dummy5 = 0;
        dummy6 = 0;
        dummy7 = 0;
        dummy8 = 0;
    }

    @Override
    public String toString(){
        String str = "["+iterationNo+" - "+distance+"]";
        return str;
    }

    public void initialize(short i, double d){
        this.iterationNo = i;
        this.distance = d;
    }

    public void reset(){
        distance = 0;
        iterationNo = 0;
    }
}


public class IterationDistancePairPool extends ObjectPool<IterationDistancePair> {

    private IterationDistancePairPool(int size){
        super(size);

        for(int i=0;i<size;i++)
            available.add(new IterationDistancePair((short) -1,(double)-1));

        //System.out.println("The inside capacity is "+size);
    }

    @Override
    protected void reset(IterationDistancePair object) {
        object.reset();
    }


    private static IterationDistancePairPool instance = null;

    public static IterationDistancePairPool createPool(int size) {

        if (instance == null)
        {
            synchronized(ObjectPool.class){
                if (instance == null){

                    //System.out.println("The IterationDistancePairPool capacity is "+size);
                    instance = new IterationDistancePairPool(size);
                    return instance;
                }
            }
        }
        return instance;
    }

    public IterationDistancePair getObject(short i, double d) throws IndexOutOfBoundsException {
        //System.out.println("Object IterationDistancePair created ("+i+","+d+")");

        IterationDistancePair object = getObject();
        object.initialize(i,d);

        //System.out.println("Object IterationDistancePair created ("+i+","+d+")");
        return object;
    }


    public void increaseSize(int size){
        for(int i=0;i<size;i++)
            available.add(new IterationDistancePair((short) -1,(double)-1));
    }

}

