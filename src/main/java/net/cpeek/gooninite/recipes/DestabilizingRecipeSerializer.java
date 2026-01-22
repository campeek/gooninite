package net.cpeek.gooninite.recipes;


import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("removal")
public class DestabilizingRecipeSerializer implements RecipeSerializer<PhaseDestabilizingRecipe> {
    @Override
    public PhaseDestabilizingRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        int time = GsonHelper.getAsInt(json, "processingTime", 100);
        int energy = GsonHelper.getAsInt(json, "energy", 1000);

        JsonObject fluidObj = json.getAsJsonObject("fluid");
        int fluidAmt = fluidObj.get("amount").getAsInt();
        String fluidType = fluidObj.get("type").getAsString();
        ResourceLocation fluidTypeID = new ResourceLocation(fluidType);
        Fluid goonJuice = ForgeRegistries.FLUIDS.getValue(fluidTypeID);

        FluidStack fluidIngredientStack = new FluidStack(goonJuice, fluidAmt);
        return new PhaseDestabilizingRecipe(id, ingredient, time, energy, fluidIngredientStack);
    }

    @Override
    public @Nullable PhaseDestabilizingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int time = buf.readVarInt();
        int energy = buf.readVarInt();
        ResourceLocation fluidID = buf.readResourceLocation();
        int fluidAmt = buf.readVarInt();

        FluidStack fluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidID), fluidAmt);

        return new PhaseDestabilizingRecipe(id, ingredient, time, energy, fluid);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, PhaseDestabilizingRecipe recipe) {
        recipe.ingredient().toNetwork(buf);
        buf.writeVarInt(recipe.processingTime());
        buf.writeVarInt(recipe.energy());
        FluidStack fluid = recipe.resultFluid();
        buf.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(fluid.getFluid()));
        buf.writeVarInt(fluid.getAmount());
    }
}
;