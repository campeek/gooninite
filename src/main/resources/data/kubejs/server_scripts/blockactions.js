/*
BlockEvents.broken('ae2:flawless_budding_quartz', event =>{
    if(event.player.mainHandItem.hasEnchantment('minecraft:silk_touch', 2)){
        event.player.give('ae2:flawless_budding_quartz')
        event.block.set('minecraft:air')
        return
    }

 event.player.tell('Try Silk Touch II B^)')

})


BlockEvents.broken('minecraft:spawner', event => {
    if(!event.player.mainHandItem.hasEnchantment('minecraft:silk_touch', 2)){
        event.player.give('kubejs:broken_spawner')
        event.block.set('minecraft:air')
        event.player.tell('Try Silk Touch II B^)')
        return
    }

    
    

})

*/