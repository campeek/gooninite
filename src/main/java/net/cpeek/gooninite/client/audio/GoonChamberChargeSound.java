package net.cpeek.gooninite.client.audio;


import net.cpeek.gooninite.GooniniteSounds;
import net.cpeek.gooninite.blocks.machines.hgc.GoonChamberPhase;
import net.cpeek.gooninite.blocks.machines.hgc.HyperbolicGoonChamberControllerBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


public class GoonChamberChargeSound extends AbstractTickableSoundInstance {

    private final BlockPos pos;
    private final Level level;

    private static final int minimumTicks = 4;

    private int tick;
    private boolean stopped = false;

    public GoonChamberChargeSound(Level level, BlockPos pos){
        super(GooniniteSounds.GOON_CHAMBER_SPIN_UP.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());

        this.level = level;
        this.pos = pos;

        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();

        this.looping = false;
        this.delay = 0;
        this.volume = 1.0f;
    }

    @Override
    public void tick() {
        tick++;
        //BlockEntity entity = level.getBlockEntity(pos);
        BlockEntity entity = Minecraft.getInstance().level.getBlockEntity(pos);
        //System.out.println(entity.getClass());
        if(entity instanceof HyperbolicGoonChamberControllerBE gc){
            //System.out.println("entity exists");
            //System.out.println(gc.getPhase().name());
            if(gc.getPhase() != GoonChamberPhase.CHARGING){
                //System.out.println("stopping sound?");
                if(tick >= minimumTicks)
                    this.stop();
            }
        }

        if(stopped)
            this.stop();
    }

    public void stopSound(){
        this.stopped = true;
    }
}
