package psammos.world.blocks.radiation;

import psammos.type.RadiationType;

public interface RadiationConsumer {
    float[] sideRadAmounts();
    RadiationType[] sideRadTypes();
}
