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
public class PressingRecipeSerializer implements RecipeSerializer<GoonPressingRecipe> {
    @Override
    public GoonPressingRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        JsonObject resultObject = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = new ItemStack(
                BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(resultObject, "item"))),
                GsonHelper.getAsInt(resultObject, "count", 1)
        );

        int time = GsonHelper.getAsInt(json, "processingTime", 100);
        int minRpm = GsonHelper.getAsInt(json, "minRpm", 0);

        return new GoonPressingRecipe(id, ingredient, time, result, minRpm);
    }

    @Override
    public @Nullable GoonPressingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        int time = buf.readVarInt();
        int minRpm = buf.readVarInt();
        return new GoonPressingRecipe(id, ingredient, time, result, minRpm);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, GoonPressingRecipe recipe) {
        recipe.ingredient().toNetwork(buf);
        buf.writeItem(recipe.resultItem());
        buf.writeVarInt(recipe.processingTime());
        buf.writeVarInt(recipe.minRPM());
    }
}
