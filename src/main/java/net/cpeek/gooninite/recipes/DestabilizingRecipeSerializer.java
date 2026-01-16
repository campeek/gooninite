package net.cpeek.gooninite.recipes;


import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("removal")
public class DestabilizingRecipeSerializer implements RecipeSerializer<PhaseDestabilizingRecipe> {
    @Override
    public PhaseDestabilizingRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        JsonObject resultObject = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = new ItemStack(
                BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(resultObject, "item"))),
                GsonHelper.getAsInt(resultObject, "count", 1)
        );

        int time = GsonHelper.getAsInt(json, "processingTime", 100);
        int energy = GsonHelper.getAsInt(json, "energy", 1000);
        int fluid = GsonHelper.getAsInt(json, "fluid");

        return new PhaseDestabilizingRecipe(id, ingredient, time, energy, fluid);
    }

    @Override
    public @Nullable PhaseDestabilizingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int time = buf.readVarInt();
        int energy = buf.readVarInt();
        int fluid = buf.readVarInt();

        return new PhaseDestabilizingRecipe(id, ingredient, time, energy, fluid);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, PhaseDestabilizingRecipe recipe) {
        recipe.ingredient().toNetwork(buf);
        buf.writeVarInt(recipe.processingTime());
        buf.writeVarInt(recipe.energy());
        buf.writeVarInt(recipe.fluid());
    }
}
