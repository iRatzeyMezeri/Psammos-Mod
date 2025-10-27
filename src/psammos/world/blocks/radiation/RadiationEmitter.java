package psammos.world.blocks.radiation;

import psammos.type.RadiationType;

public interface RadiationEmitter {
    float[] outputRadAmounts();
    float[] outputRadFrac();
    RadiationType[] outputRadTypes();
    float radBeamRange();
}
