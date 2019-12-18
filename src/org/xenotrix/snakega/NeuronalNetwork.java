package org.xenotrix.snakega;

import java.util.ArrayList;

public class NeuronalNetwork {
    private int inputs;
    private int outputs;
    private ArrayList<Integer> layers;

    public void setInputs(int count) {
        this.inputs = count;
    }

    public void setOutputs(int count) {
        this.outputs = count;
    }

    public void addHiddenLayer(int count) {
        this.layers.add(count);
    }

    /**
     * Processes the neural network and returns the output layer.
     * @return Outputs as softmax distribution
     */
    public float[] process(float[][][] weights, float[] inputs) {
        float[] prevLayer = inputs;

        for (int layerIndex = 0; layerIndex < layers.size() + 2; layerIndex++) {
            prevLayer = processLayer(prevLayer, weights[layerIndex]);
        }

        // Apply softmax
        return softmax(prevLayer);
    }

    /**
     * Returns the weighted sum of the inputs and weights.
     */
    private float[] processLayer(float[] inputs, float[][] weights) {
        float[] outputs = new float[weights.length];
        for (int outIndex = 0; outIndex < outputs.length; outIndex++) {
            int sum = 0;
            for (int inIndex = 0; inIndex < inputs.length; inIndex++) {
                sum += inputs[inIndex] * weights[inIndex][outIndex];
            }
            outputs[outIndex] = sum;
        }
        return outputs;
    }

    /**
     * Returns the softmax distribution of the inputs.
     */
    private float[] softmax(float[] values) {
        // Calculate exp for every output
        float[] exp = new float[values.length];
        for (int i = 0; i < exp.length; i++)
            exp[i] = (float)Math.exp(values[i]);

        // Sum up all exps
        float sum = 0;
        for (float v : exp) sum += v;

        // Calculate individual probability
        float[] result = new float[exp.length];
        for (int i = 0; i < result.length; i++)
            result[i] = exp[i] / sum;

        return result;
    }

}
