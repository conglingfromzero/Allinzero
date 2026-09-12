package net.congling.allinzero.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ToulingPortalParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    ToulingPortalParticle(ClientLevel level, double x, double y, double z,
                          double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.friction = 0.96F;
        this.xd = xSpeed + (this.random.nextDouble() * 2.0 - 1.0) * 0.1;
        this.yd = ySpeed + (this.random.nextDouble() * 2.0 - 1.0) * 0.1;
        this.zd = zSpeed + (this.random.nextDouble() * 2.0 - 1.0) * 0.1;
        this.lifetime = (int) (this.random.nextDouble() * 10.0) + 40;
        this.quadSize *= (float) (this.random.nextDouble() * 0.6 + 0.6);
        this.alpha = 1.0F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) this.age / (float) this.lifetime;
        this.yd += 0.002;
        this.alpha = 1.0F - progress;
        this.quadSize *= 0.995F;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= (double) this.friction;
        this.yd *= (double) this.friction;
        this.zd *= (double) this.friction;
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(net.minecraft.core.particles.SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ToulingPortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
