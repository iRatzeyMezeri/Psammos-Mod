package psammos.content.blocks;

import mindustry.type.Category;
import mindustry.world.Block;
import psammos.content.PsammosItems;
import psammos.type.RadiationType;
import psammos.world.blocks.radiation.*;

import static mindustry.type.ItemStack.with;

public class PsammosRadiationBlocks {
    public static Block
            radiationSource, radiationVoid, focuser, mirror, convexLens, concaveLens, solarCollector;

    public static void load() {
        radiationSource = new RadiationSource("radiation-source"){{
            category = Category.power;
        }};

        radiationVoid = new RadiationVoid("radiation-void"){{
            category = Category.power;
        }};

        focuser = new Focuser("focuser"){{
            requirements(Category.power, with(PsammosItems.silver, 5, PsammosItems.desertGlassShard, 3));
        }};

        mirror = new Mirror("mirror"){{
            requirements(Category.power, with(PsammosItems.silver, 7, PsammosItems.desertGlassShard, 5));
        }};

        convexLens = new Lens("convex-lens"){{
            requirements(Category.power, with(PsammosItems.osmium, 5, PsammosItems.silver, 10, PsammosItems.desertGlassShard, 20));
            concave = false;
        }};

        concaveLens = new Lens("concave-lens"){{
            requirements(Category.power, with(PsammosItems.osmium, 5, PsammosItems.silver, 10, PsammosItems.desertGlassShard, 20));
            concave = true;
        }};

        solarCollector = new SolarCollector("solar-collector") {{
            requirements(Category.power, with(PsammosItems.refinedMetal, 10, PsammosItems.silver, 30, PsammosItems.desertGlassShard, 30));
            size = 3;
            radOutputAmount = 3f;
            radOutputType = RadiationType.light;
        }};
    }
}
