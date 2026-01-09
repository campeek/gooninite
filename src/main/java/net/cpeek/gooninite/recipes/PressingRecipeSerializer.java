package net.cpeek.gooninite.recipes;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

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
        boolean heatReq = GsonHelper.getAsBoolean(json, "requiresHeat", false);

        return new GoonPressingRecipe(id, ingredient, result, time, minRpm, heatReq);
    }

    @Override
    public @Nullable GoonPressingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        int time = buf.readVarInt();
        int minRpm = buf.readVarInt();
        boolean heatReq = buf.readBoolean();
        return new GoonPressingRecipe(id, ingredient, result, time, minRpm, heatReq);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, GoonPressingRecipe recipe) {
        recipe.ingredient().toNetwork(buf);
        buf.writeItem(recipe.result());
        buf.writeVarInt(recipe.processingTime());
        buf.writeVarInt(recipe.minRpm());
        buf.writeBoolean(recipe.requiresHeat());
    }
}
