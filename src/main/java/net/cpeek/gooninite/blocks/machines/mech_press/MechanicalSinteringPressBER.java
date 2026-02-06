package net.cpeek.gooninite.blocks.machines.mech_press;


import com.mojang.blaze3d.vertex.PoseStack;
import net.cpeek.gooninite.blocks.GooniniteBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalSinteringPressBER implements BlockEntityRenderer<MechanicalSinteringPressBE> {

    //private static final ResourceLocation STATIC_MODEL = ResourceLocation.fromNamespaceAndPath("gooninite", "block/mechanical_sinter_press_static");
    private float pistonProgress = 0;

    public MechanicalSinteringPressBER(BlockEntityRendererProvider.Context context){

    }


    @Override
    public void render(MechanicalSinteringPressBE be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay){
        Minecraft mc = Minecraft.getInstance();

        BlockState ramState = GooniniteBlocks.PRESS_RAM.get().defaultBlockState();
        BakedModel ramModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(ramState);

        var dispatcher = mc.getBlockRenderer();
        ModelBlockRenderer modelRenderer = dispatcher.getModelRenderer();


        float travelUnits = 5.0f;

        float time = be.getTicksSinceLast() + partialTick;
        if(be.getPhase() == PressPhase.DESCEND){
            pistonProgress = Mth.clamp(time/be.pistonMoveTicks, 0f, 1f);
        } else if(be.getPhase() == PressPhase.ASCEND){
            pistonProgress = 1f-Mth.clamp(time/be.pistonMoveTicks, 0f, 1f);
        } else if(be.getPhase() == PressPhase.DWELL){
            pistonProgress = 1f;
        }

        //System.out.println("BER move ticks: " + be.pistonMoveTicks);

        float y = -(pistonProgress*travelUnits)/16f;
        poseStack.pushPose();
        poseStack.translate(0, y, 0);
        var vc = buffer.getBuffer(RenderType.solid());

        modelRenderer.renderModel(
                poseStack.last(),
                vc,
                ramState,
                ramModel,
                1f, 1f, 1f,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }
}
