package net.cpeek.gooninite.particles;

import net.cpeek.gooninite.GoonParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

// GoonJuiceParticle.java
// Cameron Peek
// 12/20/2025
public class GoonJuiceDrippingParticle extends TextureSheetParticle {

    private final int DRIP_HANG_TIME = 25;

    protected GoonJuiceDrippingParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);

        this.pickSprite(sprites);

        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        this.gravity = 0.0f; // how fast the drip falls
        this.friction = 1.0f;

        this.quadSize = 0.35f; // how big the drip be
        this.lifetime = 25;

        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;

        this.alpha = 0.9f;
    }

    @Override
    public void tick(){
        super.tick();

        this.quadSize *= 1.002f; // goon particle will grow over time

        if(this.age >= DRIP_HANG_TIME){
            this.level.addParticle(GoonParticles.GOON_JUICE_FALLING.get(),
                    this.x, this.y, this.z,
                    0,0,0);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites){
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz){
            return new GoonJuiceDrippingParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
