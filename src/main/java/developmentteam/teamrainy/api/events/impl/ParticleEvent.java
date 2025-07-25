package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public class ParticleEvent extends Event {
    public ParticleEvent() {
        super(Stage.Pre);
    }
    public static class AddParticle extends ParticleEvent {

        public final Particle particle;
        public AddParticle(Particle particle){
            this.particle = particle;
        }

    }

    public static class AddEmmiter extends ParticleEvent {
        public final ParticleEffect emmiter;

        public AddEmmiter(ParticleEffect emmiter){
            this.emmiter = emmiter;
        }

    }
}