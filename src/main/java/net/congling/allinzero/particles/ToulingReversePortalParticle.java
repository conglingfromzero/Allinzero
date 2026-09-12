package net.congling.allinzero.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ToulingReversePortalParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    ToulingReversePortalParticle(ClientLevel level, double x, double y, double z,
                                 double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.friction = 1.0F;
        this.xd = xSpeed * 0.4;
        this.yd = ySpeed * 0.4;
        this.zd = zSpeed * 0.4;
        this.lifetime = (int) (this.random.nextDouble() * 10.0) + 40;
        this.quadSize *= 0.75F;
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
        double factor = 1.0 - progress * 2.0;
        this.move(this.xd * factor, this.yd * factor, this.zd * factor);
        this.quadSize *= 0.985F;
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
            return new ToulingReversePortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
