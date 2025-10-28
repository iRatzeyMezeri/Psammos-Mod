package psammos.world.blocks.radiation;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;
import psammos.graphics.PDraw;
import psammos.type.RadiationStack;
import psammos.type.RadiationType;

import static mindustry.Vars.*;

public class RadiationUtil {

    /** Gets the center point of the specified side of the build*/
    public static Point2 side(Building build, int rotation){
        int x = build.tileX() + Geometry.d4x[rotation] * Mathf.ceil(build.block.size / 2f);
        int y = build.tileY() + Geometry.d4y[rotation] * Mathf.ceil(build.block.size / 2f);
        return new Point2(x, y);
    }

    public static RadiationStack[] calculateSideRadiation(Building build){
        //TODO
        return new RadiationStack[4];
    }

    /** Finds the tile radiation hits if the building emits it in the specified direction.
     * Returns null if no target was found */
    public static Tile findRadiationTarget(Building build, int rotation){
        if (!(build instanceof RadiationEmitter)){
            return null;
        }
        RadiationEmitter emitter = (RadiationEmitter) build;

        int x = side(build, rotation).x;
        int y = side(build, rotation).y;
        int dist = 0;
        while (!(world.tile(x, y) == null ||
                world.tile(x, y).block() instanceof StaticWall ||
                world.tile(x, y).build instanceof RadiationConsumer)){
            x += Geometry.d4x(rotation);
            y += Geometry.d4y(rotation);
            dist++;
            if (dist >= emitter.radBeamRange()){
                return null;
            }
        }
        return world.tile(x, y);
    }

    public static void drawRadiationBeams(Building build){
        if (!(build instanceof RadiationEmitter)){
            return;
        }
        RadiationEmitter emitter = (RadiationEmitter) build;

        TextureRegion beam = Core.atlas.find("psammos-radiation-beam");
        TextureRegion beamEnd = Core.atlas.find("psammos-radiation-beam-end");

        for (int rotation = 0; rotation < 4; rotation++){
            RadiationStack radStack = emitter.outputRadiation()[rotation];
            float amountFraction = emitter.outputRadiationFrac()[rotation];

            if (radStack == null || radStack.type == null || amountFraction == 0){
                continue;
            }

            Tile target = RadiationUtil.findRadiationTarget(build, rotation);
            float dx = Geometry.d4x[rotation] * tilesize;
            float dy = Geometry.d4y[rotation] * tilesize;
            Color color = radStack.type.color.cpy().a(amountFraction);
            Draw.z(Layer.effect);

            if (target != null){
                Draw.color(color);
                Drawf.laser(beam, beamEnd,
                        build.x + dx * build.block.size / 2f, build.y + dy * build.block.size / 2f,
                        target.worldx() - dx / 2f, target.worldy() - dy / 2f,
                        0.4f);
            }else{
                PDraw.gradientLaser(beam, beamEnd,
                        build.x + dx * build.block.size / 2f, build.y + dy * build.block.size / 2f, color,
                        build.x + dx * emitter.radBeamRange() * 1.2f, build.y + dy * emitter.radBeamRange() * 1.2f, Color.clear,
                        0.4f);
            }
            Draw.reset();
        }
    }
}
