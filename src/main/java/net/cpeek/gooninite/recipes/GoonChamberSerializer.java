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

public class GoonChamberSerializer implements RecipeSerializer<HyperbolicGoonificationRecipe> {
    @Override
    public HyperbolicGoonificationRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        int ingCount = GsonHelper.getAsInt(json, "ingredient_count", 4);

        JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = new ItemStack(
                BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(resultJson, "item"))),
                GsonHelper.getAsInt(resultJson, "count", 1)
        );

        int time = GsonHelper.getAsInt(json, "processingTime");
        int energy = GsonHelper.getAsInt(json, "energy");

        JsonObject fluidJson = GsonHelper.getAsJsonObject(json,"fluid");
        int fluidAmt = fluidJson.get("amount").getAsInt();
        String fluidType = fluidJson.get("type").getAsString();

        ResourceLocation fluidTypeID = new ResourceLocation(fluidType);
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidTypeID);
        FluidStack fluidStack = new FluidStack(fluid, fluidAmt);

        return new HyperbolicGoonificationRecipe(id, ingredient, ingCount, time, energy, fluidStack, result);
    }

    @Override
    public @Nullable HyperbolicGoonificationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ing = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        int amountItem = buf.readVarInt();

        int time = buf.readVarInt();
        int energy = buf.readVarInt();

        ResourceLocation fluidID = buf.readResourceLocation();
        int fluidAmt = buf.readVarInt();
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidID);

        FluidStack ingFluid = new FluidStack(fluid, fluidAmt);

        return new HyperbolicGoonificationRecipe(id, ing, amountItem, time, energy, ingFluid, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, HyperbolicGoonificationRecipe recipe) {
        recipe.ingredient().toNetwork(buf);

        buf.writeItem(recipe.resultItem());
        buf.writeVarInt(recipe.ingCount());

        buf.writeVarInt(recipe.processingTime());
        buf.writeVarInt(recipe.energy());

        FluidStack ingFluid = recipe.ingredientFluid();
        buf.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(ingFluid.getFluid()));
        buf.writeVarInt(ingFluid.getAmount());
    }
}
