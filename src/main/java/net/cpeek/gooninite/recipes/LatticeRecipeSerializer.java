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
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

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
        int fluid = GsonHelper.getAsInt(json, "fluid");

        return new LatticeRecrystallizingRecipe(id, ingredient, result, time, energy, fluid);
    }

    @Override
    public @Nullable LatticeRecrystallizingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        int time = buf.readVarInt();
        int energy = buf.readVarInt();
        int fluid = buf.readVarInt();

        return new LatticeRecrystallizingRecipe(id, ingredient, result, time, energy, fluid);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, LatticeRecrystallizingRecipe recipe) {
        recipe.ingredient().toNetwork(buf);
        buf.writeItem(recipe.result());
        buf.writeVarInt(recipe.processsingTime());
        buf.writeVarInt(recipe.energy());
        buf.writeVarInt(recipe.fluid());
    }
}
