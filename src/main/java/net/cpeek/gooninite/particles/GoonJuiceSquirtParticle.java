package net.cpeek.gooninite.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class GoonJuiceSquirtParticle extends TextureSheetParticle {

    protected GoonJuiceSquirtParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);

        this.pickSprite(sprites);

        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        this.gravity = 0.29f; // how fast the drip falls
        this.friction = 0.98f;

        this.quadSize = 0.25f; // how big the drip be
        this.lifetime = 500;

        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;

        this.alpha = 0.9f;
    }

    @Override
    public void tick(){
        super.tick();

        //this.quadSize *= 0.99f; // goon particles shrink over time

        if(this.age > this.lifetime - 6){ // goon juice dries out over time
            this.alpha *= 0.8f;
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
            return new GoonJuiceSquirtParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
