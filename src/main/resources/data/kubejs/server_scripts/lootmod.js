LootJS.modifiers((event) => {
    event.addBlockLootModifier('minecraft:spawner')
    .addWeightedLoot([
        Item.of('4x kubejs:broken_spawner').withChance(5),
        Item.of('3x kubejs:broken_spawner').withChance(25),
        Item.of('2x kubejs:broken_spawner').withChance(45),
        Item.of('kubejs:broken_spawner').withChance(85),
    ])
    event
    .addBlockLootModifier('ae2:flawless_budding_quartz')
    .matchMainHand(ItemFilter.hasEnchantment('minecraft:silk_touch',2,2))
    .addLoot('ae2:flawless_budding_quartz')
    .removeLoot('ae2:flawed_budding_quartz')


    event.addLootTableModifier('minecraft:chests/ancient_city').randomChance(0.15).addLoot('gooninite:goon_smithing_template')

})