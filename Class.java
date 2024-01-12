public class Class {
    private String className;
    private int capacity;

    public Class(String className, int capacity){
        this.capacity=capacity;
        this.className=className;
    }




    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
