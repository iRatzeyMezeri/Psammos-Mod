package psammos.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import psammos.graphics.*;
import psammos.type.RadiationStack;
import psammos.world.blocks.radiation.*;

import static mindustry.Vars.tilesize;

public class DrawRadiationBeams extends DrawBlock {
    public TextureRegion beam, beamEnd;

    @Override
    public void load(Block block) {
        super.load(block);
        beam = Core.atlas.find("psammos-radiation-beam");
        beamEnd = Core.atlas.find("psammos-radiation-beam-end");
    }

    @Override
    public void draw(Building build) {
        if (!(build instanceof RadiationEmitter emitter)){
            return;
        }

        Draw.z(Layer.effect);
        for (int rotation = 0; rotation < 4; rotation++){
            RadiationStack radStack = emitter.outputRadiation()[rotation];

            if (radStack == null || radStack.type == null || radStack.amount == 0){
                continue;
            }

            Tile target = RadiationUtil.findRadiationTarget(build, rotation);
            float dx = Geometry.d4x[rotation] * tilesize;
            float dy = Geometry.d4y[rotation] * tilesize;
            Color color = radStack.type.color.cpy();
            float scale = Mathf.clamp(radStack.amount / 100f) *  0.5f;

            if (target != null){
                Draw.color(color);
                Drawf.laser(beam, beamEnd,
                        build.x + dx * build.block.size / 2f, build.y + dy * build.block.size / 2f,
                        target.worldx() - dx / 2f, target.worldy() - dy / 2f,
                        scale);
                Draw.color();
            }else{
                PDraw.gradientLaser(beam, beamEnd,
                        build.x + dx * build.block.size / 2f, build.y + dy * build.block.size / 2f, color,
                        build.x + dx * emitter.radBeamRange() * 1.2f, build.y + dy * emitter.radBeamRange() * 1.2f, color.cpy().a(0),
                        scale);
            }
        }
        Draw.z(Layer.block);
    }
}
