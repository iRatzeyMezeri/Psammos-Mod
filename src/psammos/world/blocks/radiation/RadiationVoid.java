package psammos.world.blocks.radiation;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

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
        public void addRadiationInput(Building build) {}
    }
}
