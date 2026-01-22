package net.cpeek.gooninite.recipes;


import net.cpeek.gooninite.Gooninite;
import net.cpeek.gooninite.fluids.GooniniteFluids;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;


// TODO: change this recipe to give a FluidStack and amount
public class LatticeRecrystallizingRecipe extends BaseGoonRecipe implements IGoonItemRecipe, IGoonFluidConsumer {


    private FluidStack fluidIngredient;
    private ItemStack resultItem;

    public LatticeRecrystallizingRecipe(ResourceLocation id, Ingredient ing, FluidStack ingFluid, int processingTime, int energy, ItemStack resultItem) {
        super(id, ing, processingTime, energy);

        fluidIngredient = ingFluid;
        this.resultItem = resultItem;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return resultItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return resultItem;
    }

    public ItemStack getOutputItem(){
        return resultItem;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GooniniteRecipes.RECRYSTALLIZING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GooniniteRecipes.LATTICE_RECRYSTALLIZING_RECIPE.get();
    }

    @Override
    public FluidStack ingredientFluid() {
        return fluidIngredient;
    }

    @Override
    public ItemStack resultItem() {
        return resultItem;
    }
}
