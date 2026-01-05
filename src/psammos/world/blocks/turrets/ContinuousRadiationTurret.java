package psammos.world.blocks.turrets;

import arc.struct.*;
import mindustry.entities.bullet.*;
import mindustry.gen.Building;
import mindustry.world.blocks.defense.turrets.*;
import psammos.type.RadiationStack;
import psammos.type.RadiationType;
import psammos.world.blocks.radiation.RadiationConsumer;

public class ContinuousRadiationTurret extends ContinuousTurret {
    public ObjectMap<RadiationType, BulletType> ammoTypes = new ObjectMap<>();
    public float radiationRequirement = 10f;
    public float maxRadiationEfficiency = 3f;

    public ContinuousRadiationTurret(String name) {
        super(name);
    }

    public void ammo(Object... objects){
        ammoTypes = ObjectMap.of(objects);
    }

    @Override
    public void setStats() {
        super.setStats();
        // TODO
    }

    @Override
    public void init() {
        if(targetGround){
            ammoTypes.each((rad, type) -> placeOverlapRange = Math.max(placeOverlapRange, range + type.rangeChange + placeOverlapMargin));
        }

        super.init();
    }

    public class ContinuousRadiationTurretBuild extends ContinuousTurretBuild implements RadiationConsumer {
        public Seq<Building> radiationInputs = new Seq<>();
        public RadiationStack currentRadiation = new RadiationStack(null, 0);
        boolean activated;

        @Override
        public boolean shouldActiveSound(){
            return wasShooting && enabled;
        }

        @Override
        public void updateTile(){
            super.updateTile();

            currentRadiation = calculateHighestRadiation(this, radiationInputs);

            unit.ammo(unit.type().ammoCapacity * currentRadiation.amount / radiationRequirement);

            activated = currentRadiation.amount > 0f;
        }

        @Override
        public void updateEfficiencyMultiplier(){
            super.updateEfficiencyMultiplier();
            efficiency *= Math.min(currentRadiation.amount / radiationRequirement, maxRadiationEfficiency);
        }

        @Override
        public boolean canConsume(){
            return currentRadiation.amount > 0f && hasCorrectAmmo() && super.canConsume();
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && activated;
        }

        @Override
        public BulletType useAmmo(){
            //does not consume ammo upon firing
            return peekAmmo();
        }

        @Override
        public BulletType peekAmmo(){
            if (currentRadiation == null || currentRadiation.type == null) {
                return null;
            }
            return ammoTypes.get(currentRadiation.type);
        }

        @Override
        public boolean hasAmmo(){
            if (currentRadiation == null || currentRadiation.type == null) {
                return false;
            }
            return hasCorrectAmmo() && ammoTypes.get(currentRadiation.type) != null && currentRadiation.amount > 0f;
        }

        public boolean hasCorrectAmmo(){
            return !bullets.any() || bullets.first().bullet.type == peekAmmo();
        }

        @Override
        public void addRadiationInput(Building build) {
            if (!radiationInputs.contains(build)){
                radiationInputs.add(build);
            }
        }

        @Override
        public boolean acceptsRadiation(RadiationType type, int from) {
            return ammoTypes.containsKey(type);
        }
    }
}
