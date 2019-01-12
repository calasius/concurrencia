package counter;

public class NonSecureCounter implements Counter {

    private int value;

    public NonSecureCounter() {
        this.value = 0;
    }

    @Override
    public void increment() {
        this.value ++;
    }

    @Override
    public void decrement() {
        this.value --;
    }

    @Override
    public int value() {
        return this.value;
    }
}
