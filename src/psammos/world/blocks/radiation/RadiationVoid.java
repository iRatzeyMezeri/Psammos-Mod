package psammos.world.blocks.radiation;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import psammos.type.RadiationType;

public class RadiationVoid extends Block {

    public RadiationVoid(String name) {
        super(name);
        update = true;
        solid = true;
        envEnabled = Env.any;
        buildVisibility = BuildVisibility.sandboxOnly;
    }

    public class RadiationVoidBuild extends Building implements RadiationConsumer {

        @Override
        public float[] sideRadAmounts() {
            return new float[0];
        }

        @Override
        public RadiationType[] sideRadTypes() {
            return new RadiationType[0];
        }
    }
}
