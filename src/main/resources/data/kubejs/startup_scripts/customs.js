/*
StartupEvents.registry('item', event => {
    event.create('craftingstick')
    .displayName('Crafting Stick')
    
})
    

*/

StartupEvents.registry('item', event => {
    event.create('broken_spawner')
    .displayName('Broken Spawner')
    .texture('minecraft:block/spawner')
    
    event.create('incomplete_spore_blossom', 'create:sequenced_assembly')

    //temp goon
    event.create('test_sword', 'sword')
    .displayName('Gooninite Sword')
    .texture('kubejs:item/testsword')

    event.create('test_axe', 'axe')
    .displayName('Gooninite Axe')
    .texture('kubejs:item/testaxe')

    event.create('test_pick', 'pickaxe')
    .displayName('Gooninite Pickaxe')
    .texture('kubejs:item/testpick')

    
})
/*StartupEvents.registry('item', event => {
	event.create('incomplete_spore_blossom', 'create:sequenced_assembly')
    })*/


StartupEvents.registry('block', event=> {
    event.create('energized_quartz_glass')
    .displayName('Energized Quartz Glass')
    .soundType('glass')
    .mapColor('glass')
   // .model('kubejs:block/quartz_glass')
    .textureAll('kubejs:block/quartz_glass')
    .defaultCutout()
    //.transparent(true)
    //.defaultTranslucent()
    .item(itemBuilder => itemBuilder.glow(true))

    event.create('gooninite_block')
    .displayName('Gooninite Block')
    .textureAll('kubejs:block/testblock')
    .defaultCutout()

    event.create('energized_gooninite_block')
    .displayName('Energized Gooninite Block')
    .textureAll('kubejs:block/testblock')
    .defaultCutout()
    .item(itemBuilder => itemBuilder.glow(true))
})