package net.cpeek.gooninite.recipes;


import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("removal")
public class LatticeRecipeSerializer implements RecipeSerializer<LatticeRecrystallizingRecipe> {
    @Override
    public LatticeRecrystallizingRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        JsonObject resultObject = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = new ItemStack(
                BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(resultObject, "item"))),
                GsonHelper.getAsInt(resultObject, "count", 1)
        );

        int time = GsonHelper.getAsInt(json, "processingTime", 100);
        int energy = GsonHelper.getAsInt(json, "energy", 1000);

        JsonObject fluidObj = json.getAsJsonObject("fluid");
        int fluidAmt = fluidObj.get("amount").getAsInt();
        String fluidType = fluidObj.get("type").getAsString();
        ResourceLocation fluidTypeID = new ResourceLocation(fluidType);
        Fluid goonJuice = ForgeRegistries.FLUIDS.getValue(fluidTypeID);

        FluidStack fluid = new FluidStack(goonJuice, fluidAmt);

        return new LatticeRecrystallizingRecipe(id, ingredient, fluid, time, energy, result);
    }

    @Override
    public @Nullable LatticeRecrystallizingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        int time = buf.readVarInt();
        int energy = buf.readVarInt();
        ResourceLocation fluidID = buf.readResourceLocation();
        int fluidAmt = buf.readVarInt();

        FluidStack fluid = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidID), fluidAmt);

        return new LatticeRecrystallizingRecipe(id, ingredient, fluid, time, energy, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, LatticeRecrystallizingRecipe recipe) {
        recipe.ingredient().toNetwork(buf);
        buf.writeItem(recipe.resultItem());
        buf.writeVarInt(recipe.processingTime());
        buf.writeVarInt(recipe.energy());

        FluidStack fluid = recipe.ingredientFluid();
        buf.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(fluid.getFluid()));
        buf.writeVarInt(fluid.getAmount());
    }
}
