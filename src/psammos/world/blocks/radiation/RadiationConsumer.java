package psammos.world.blocks.radiation;

import mindustry.gen.Building;
import psammos.type.RadiationType;

public interface RadiationConsumer {
    void addRadiationInput(Building build);
    boolean acceptsRadiation(RadiationType type, int from);
    default float incomingBeamOffset() {
        return 0;
    }
}
