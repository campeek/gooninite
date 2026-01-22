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
import net.minecraftforge.fluids.FluidStack;

public class GoonPressingRecipe extends BaseGoonRecipe implements IGoonItemRecipe, IGoonRPMConsumer{


    private ItemStack resultItem;
    private int minRPM;

    public GoonPressingRecipe(ResourceLocation id, Ingredient ing, int processingTime, ItemStack resultItem, int minRPM){
        super(id, ing, processingTime, 0);
        this.resultItem = resultItem;
        this.minRPM = minRPM;
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

    public Ingredient getInputItem(){
        return ingredient;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GooniniteRecipes.PRESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GooniniteRecipes.GOON_PRESSING_RECIPE.get();
    }

    @Override
    public ItemStack resultItem() {
        return resultItem;
    }

    @Override
    public int minRPM() {
        return minRPM;
    }
}
