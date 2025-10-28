package psammos.world.blocks.radiation;

import psammos.type.RadiationStack;

public interface RadiationEmitter {
    RadiationStack[] outputRadiation();
    float[] outputRadiationFrac();
    float radBeamRange();
}
