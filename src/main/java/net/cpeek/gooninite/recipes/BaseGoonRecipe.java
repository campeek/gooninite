package net.cpeek.gooninite.recipes;


import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class BaseGoonRecipe implements Recipe<SimpleContainer> {

    public ResourceLocation id;
    public Ingredient ingredient;
    public int processingTime;
    public int energy;

    public BaseGoonRecipe(ResourceLocation id, Ingredient ing, int processingTime, int energy){
        this.id = id;
        this.ingredient = ing;
        this.processingTime = processingTime;
        this.energy = energy;
    }

    @Override
    public boolean matches(SimpleContainer container, Level pLevel) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public ResourceLocation id(){
        return id;
    }

    public Ingredient ingredient(){
        return ingredient;
    }

    public int processingTime(){
        return processingTime;
    }

    public int energy(){
        return energy;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract RecipeType<?> getType();
}
