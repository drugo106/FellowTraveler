package com.example.fellowtraveler;

class LowPassFilter {
    private double alpha;
    private double output;
    private boolean initialized;

    public LowPassFilter(double alpha) {
        this.alpha = alpha;
        this.output = 0;
        this.initialized = false;
    }

    public double filter(double input) {
        if (!initialized) {
            output = input;
            initialized = true;
        } else {
            output = alpha * input + (1 - alpha) * output;
        }
        return output;
    }


}
