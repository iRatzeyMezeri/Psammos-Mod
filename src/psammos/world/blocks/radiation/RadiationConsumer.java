package psammos.world.blocks.radiation;

import arc.struct.Seq;
import mindustry.gen.Building;
import psammos.type.RadiationStack;
import psammos.type.RadiationType;

public interface RadiationConsumer {
    void addRadiationInput(Building build);
    boolean acceptsRadiation(RadiationType type, int from);
    default float incomingBeamOffset() {
        return 0;
    }

    default RadiationStack[] calculateSideRadiation(Building build, Seq<Building> inputs){
        RadiationStack[] sideRadiation = new RadiationStack[4];
        inputs.forEach(b -> {
            if (!(b instanceof RadiationEmitter emitter) || b.dead){
                return;
            }
            int rotation = build.relativeTo(b);

            if (emitter.outputRadiation()[(rotation + 2) % 4] != null){
                if (sideRadiation[rotation] == null){
                    sideRadiation[rotation] = new RadiationStack(emitter.outputRadiation()[(rotation + 2) % 4].type, 0);
                }
                if (sideRadiation[rotation].type == emitter.outputRadiation()[(rotation + 2) % 4].type){
                    sideRadiation[rotation].amount += emitter.outputRadiation()[(rotation + 2) % 4].amount;
                }
            }
        });
        inputs.clear();
        return sideRadiation;
    }
}
