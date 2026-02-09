package net.cpeek.gooninite;


import net.cpeek.gooninite.items.GooniniteItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;


public class GooniniteArmorMaterials {
    public static final ArmorMaterial GOON_ARMOR = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(ArmorItem.Type pType) {
            return 69;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type pType) {
            return switch (pType) {
                case HELMET -> 4;
                case CHESTPLATE -> 12;
                case LEGGINGS -> 10;
                case BOOTS -> 5;
            };
        }

        @Override
        public int getEnchantmentValue() {
            return 21;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_NETHERITE;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(GooniniteItems.GOONINITE_INGOT.get());
        }

        @Override
        public String getName() {
            return "gooninite:goon_armor";
        }

        @Override
        public float getToughness() {
            return 4.0f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.3f;
        }
    };
}
