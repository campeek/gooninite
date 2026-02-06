package net.cpeek.gooninite.recipes;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class HyperbolicGoonificationRecipe extends BaseGoonRecipe implements IGoonFluidConsumer, IGoonItemRecipe{

    private FluidStack fluidIngredient;
    private ItemStack resultItem;

    private int ingCount;

    public HyperbolicGoonificationRecipe(ResourceLocation id, Ingredient ing, int ingCount, int processingTime, int energy, FluidStack fluidIngredient, ItemStack resultItem) {
        super(id, ing, processingTime, energy);

        this.fluidIngredient = fluidIngredient;
        this.resultItem = resultItem;

        this.ingCount = ingCount;
    }

    @Override
    public boolean matches(SimpleContainer container, Level pLevel) {
        ItemStack recipeStack = new ItemStack(ingredient.getItems()[0].getItem(),ingCount);
        ItemStack containerStack = container.getItem(0);
        if(ItemStack.matches(recipeStack, containerStack)){
            return true;
        }
        return false;
    }

    @Override
    public FluidStack ingredientFluid() {
        return fluidIngredient;
    }

    @Override
    public ItemStack resultItem() {
        return resultItem.copy();
    }

    public int ingCount(){
        return ingCount;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GooniniteRecipes.GOONIFICATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GooniniteRecipes.GOONIFICATION_RECIPE.get();
    }
}
