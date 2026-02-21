/* 
 * ServerEvents.recipes(callback) is a function that accepts another function,
 * called the "callback", as a parameter. The callback gets run when the 
 * server is working on recipes, and then we can make our own changes.
 * When the callback runs, it is also known as the event "firing". 
*/

// Listen for the "recipes" server event.
ServerEvents.recipes(event => {


   event.replaceInput({output: 'create:industrial_iron_block'}, 'minecraft:iron_ingot', 'minecraft:iron_block')
   event.replaceOutput({output: 'create:industrial_iron_block'}, 'create:industrial_iron_block', 'create:industrial_iron_block')

  //packing tape
  event.replaceInput(
    { output: 'sophisticatedstorage:packing_tape' },
    'minecraft:slime_ball',
    'minecraft:ender_pearl'
  )

  //powah
  event.custom({
    type: "powah:energizing",
    ingredients: [
      {
        item: 'ae2:quartz_glass'
      },
    ],
    energy: 10000,
    result: {
      item: "kubejs:energized_quartz_glass"
    }
  });
  event.custom({
    type: "powah:energizing",
    ingredients: [
      {
        item: 'kubejs:gooninite_block'
      },
    ],
    energy: 1000000,
    result: {
      item: "kubejs:energized_gooninite_block"
    }
  });

  //Structure compass
  event.remove({output: 'structurecompass:structure_compass'})
  event.shaped(
    Item.of('structurecompass:structure_compass', 1),
    [
      'ABC',
      'DEF',
      'GHI'
    ],
    {
      A: 'minecraft:snow_block',
      B: 'easy_villagers:villager',
      C: 'minecraft:sandstone',
      D: '#forge:rare_log',
      E: 'minecraft:compass',
      F: 'minecraft:purpur_block',
      G: 'the_bumblezone:carvable_wax_flower',
      H: 'minecraft:emerald_block',
      I: 'minecraft:wither_skeleton_skull'
    }
  )

  //Nature Compass
  event.remove({output: 'naturescompass:naturescompass'})
  event.shaped(
    Item.of('naturescompass:naturescompass', 1),
    [
      'ABC',
      'DEF',
      'GHI'
    ],
    {
      A: 'minecraft:magma_block',
      B: 'minecraft:shroomlight',
      C: 'minecraft:pumpkin',
      D: 'minecraft:oak_sapling',
      E: 'minecraft:compass',
      F: 'minecraft:birch_sapling',
      G: 'minecraft:chorus_fruit',
      H: 'minecraft:water_bucket',
      I: '#create_ultimate_factory:corals'
    }
  )


  //Storage Drawers
  event.replaceInput(
    {output: 'storagedrawers:magnet_upgrade'},
    'storagedrawers:hopper_upgrade',
    'minecraft:ender_pearl'
  )
  event.replaceInput(
    {output: 'storagedrawers:magnet_upgrade'},
    'minecraft:iron_bars',
    'storagedrawers:upgrade_template'
  )
  event.replaceInput(
    {output: 'storagedrawers:magnet_upgrade_2'},
    'minecraft:iron_bars',
    'minecraft:diamond'
  )
  event.replaceInput(
    {output: 'storagedrawers:magnet_upgrade_3'},
    'minecraft:iron_bars',
    'minecraft:emerald' 
  )
  event.remove({output: 'storagedrawers:void_upgrade'})
  event.shaped(
    Item.of('storagedrawers:void_upgrade', 1),
    [
      'DCD',
      'DAD',
      'DBD'
    ],
    {
      A: 'storagedrawers:upgrade_template',
      B: 'trashcans:item_trash_can',
      C: 'minecraft:ender_pearl',
      D: '#forge:stone'
    }
  )

  //Sophisticated Storage
  event.replaceInput(
    {input: 'minecraft:fermented_spider_eye'},
    'minecraft:fermented_spider_eye',
    'minecraft:golden_apple'
  )
  event.replaceInput(
    {output: 'sophisticatedbackpacks:stack_upgrade_tier_4'},
    'minecraft:netherite_block',
    'minecraft:netherite_ingot'
  )

  event.remove({output:'sophisticatedstorage:iron_shulker_box'})
  event.shaped(
    Item.of('sophisticatedstorage:iron_shulker_box', 1), 
    [
      'AAA',
      'ABA',
      'AAA'
    ],
    { A: 'minecraft:iron_block',
      B: 'minecraft:shulker_box'
    }
  )
  event.shapeless(
    Item.of('sophisticatedstorage:iron_shulker_box'),
    [
      '2x minecraft:shulker_shell',
      'sophisticatedstorage:iron_chest'
    ]
  )  
  event.remove({output:'sophisticatedstorage:gold_shulker_box'})
  event.shaped(
    Item.of('sophisticatedstorage:gold_shulker_box', 1), 
    [
      'AAA',
      'ABA',
      'AAA'
    ],
    { A: 'minecraft:gold_block',
      B: 'minecraft:shulker_box'
    }
  )
  event.shapeless(
    Item.of('sophisticatedstorage:gold_shulker_box'),
    [
      '2x minecraft:shulker_shell',
      'sophisticatedstorage:gold_chest'
    ]
  )
  event.remove({output:'sophisticatedstorage:diamond_shulker_box'})
  event.shaped(
    Item.of('sophisticatedstorage:diamond_shulker_box', 1), 
    [
      'AAA',
      'ABA',
      'AAA'
    ],
    { A: 'minecraft:diamond_block',
      B: 'minecraft:shulker_box'
    }
  )
  event.shapeless(
    Item.of('sophisticatedstorage:diamond_shulker_box'),
    [
      '2x minecraft:shulker_shell',
      'sophisticatedstorage:diamond_chest'
    ]
  )
  
  //Waystone
  event.remove({output: 'waystones:warp_stone'})
  event.shaped(
    Item.of('waystones:warp_stone'),
    [
      'CBC',
      'BAB',
      'CBC'
    ],
    {
      A: 'minecraft:ender_pearl',
      B: 'minecraft:amethyst_shard',
      C: 'ae2:certus_quartz_crystal'
    }
  )

  //Botany pots
  event.replaceInput(    
    {output: '#botanypots:all_botany_pots'},
    'minecraft:nether_star',
    (Item.of('minecraft:enchanted_book').enchant('minecraft:fortune',2)).strongNBT()
    )
  event.replaceInput(
    {output: '#botanypots:all_botany_pots'},
    'minecraft:diamond_block',
    'minecraft:diamond'
    )

  event.replaceInput(
    {output: '#botanypots:all_botany_pots'},
    'minecraft:enchanted_golden_apple',
    (Item.of('minecraft:enchanted_book').enchant('minecraft:fortune',3)).strongNBT()
  )
  event.replaceInput(
    {output: '#botanypots:all_botany_pots'},
    'minecraft:netherite_block',
    'minecraft:netherite_ingot'
  )
  
  //Mekanism
  event.remove({id: 'mekanism:sps_casing'})
  event.remove({output: 'mekanism:reprocessed_fissile_fragment'})
  event.remove({output: 'mekanism:supercharged_coil'})
  event.remove({output: 'mekanism:module_vein_mining_unit'})

  event.remove({output: 'mekanism:jetpack'})
  event.shaped(
    Item.of('mekanism:jetpack'),
    [
      'ABA',
      'CDC',
      'C C'
    ],
    {
      A: 'powah:blazing_crystal_block',
      B: 'mekanism:ultimate_control_circuit',
      C: 'mekanism:block_steel',
      D: 'mekanism:basic_chemical_tank'
    }

  )

  event.replaceInput(
    {output: 'mekanism:module_fortune_unit'},
    'mekanism:block_refined_glowstone',
    (Item.of('minecraft:enchanted_book').enchant('minecraft:fortune',3)).strongNBT()
  )

  event.replaceInput(
    {output: 'mekanism:module_fortune_unit'},
    'minecraft:diamond_block',
    'mekanism:block_refined_glowstone'
  )

  event.replaceInput(
    {output: 'mekanism:module_magnetic_attraction_unit'},
    'minecraft:iron_bars',
    'mekanism:teleportation_core'
  )

  event.replaceInput(
    {input: 'mekanism:pellet_polonium'},
    'mekanism:pellet_polonium',
    'powah:crystal_nitro'
  )
  
  event.replaceInput(
    {output: 'mekanism:teleportation_core'},
    'minecraft:gold_ingot',
    'minecraft:ender_pearl'
  )

  event.replaceInput(
    {output: 'mekanism:chemical_crystallizer'},
    'mekanism:fluorite_gem',
    'ae2:certus_quartz_crystal'
  )
  const transitional = 'kubejs:incomplete_spore_blossom'
  event.remove({output: 'mekanism:advanced_control_circuit'})
  event.recipes.create.sequenced_assembly(
    [
      Item.of('mekanism:advanced_control_circuit').withChance(269),
      Item.of('mekanism:basic_control_circuit').withChance(3),
      Item.of('2x mekanism:alloy_infused').withChance(6)
    ],
    'mekanism:basic_control_circuit',
    [
      event.recipes.createDeploying(transitional, [transitional, 'mekanism:alloy_infused']),
      event.recipes.createPressing(transitional, transitional),
      event.recipes.createDeploying(transitional, [transitional, 'mekanism:alloy_infused']),
      event.recipes.createPressing(transitional, transitional)
    ]
  ).transitionalItem(transitional)
  .loops(1)

  event.remove({output: 'mekanism:elite_control_circuit'})
  event.recipes.create.sequenced_assembly(
    [
      Item.of('mekanism:elite_control_circuit').withChance(469),
      Item.of('mekanism:advanced_control_circuit').withChance(3),
      Item.of('2x mekanism:alloy_reinforced').withChance(6)
    ],
    'mekanism:advanced_control_circuit',
    [
      event.recipes.createDeploying(transitional, [transitional, 'mekanism:alloy_reinforced']),
      event.recipes.createPressing(transitional, transitional),
      event.recipes.createDeploying(transitional, [transitional, 'mekanism:alloy_reinforced']),
      event.recipes.createPressing(transitional, transitional)
    ]
  ).transitionalItem(transitional)
  .loops(1)

  event.remove({output: 'mekanism:ultimate_control_circuit'})
  event.recipes.create.sequenced_assembly(
    [
      Item.of('mekanism:ultimate_control_circuit').withChance(669),
      Item.of('mekanism:elite_control_circuit').withChance(3),
      Item.of('2x mekanism:alloy_atomic').withChance(6)
    ],
    'mekanism:elite_control_circuit',
    [
      event.recipes.createDeploying(transitional, [transitional, 'mekanism:alloy_atomic']),
      event.recipes.createPressing(transitional, transitional),
      event.recipes.createDeploying(transitional, [transitional, 'mekanism:alloy_atomic']),
      event.recipes.createPressing(transitional, transitional)
    ]
  ).transitionalItem(transitional)
  .loops(1)

  event.remove({output: 'gooninite:gooninite_helmet'})
  event.recipes.create.sequenced_assembly(
    [
      Item.of('gooninite:gooninite_helmet').withChance(456)
    ],
    'minecraft:netherite_helmet',
    [
      event.recipes.createDeploying(transitional, [transitional, 'gooninite:gooninite_ingot']),
      event.recipes.createPressing(transitional, transitional),
      event.recipes.createFilling(transitional, [transitional, Fluid.of('gooninite:goon_juice', 1000)])
    ]
  )
  .transitionalItem(transitional)
  .loops(1)
  
  
  event.replaceInput(
    {output: 'mekanism:module_elytra_unit'},
    'mekanism:pellet_polonium',
    'mekanism:elite_control_circuit'
  )
  event.replaceInput(
    {output: 'mekanism:module_elytra_unit'},
    'mekanism:pellet_antimatter',
    'ae2:singularity'
  )

  event.remove({output: 'mekanism:module_teleportation_unit'})
  event.recipes.shaped(
    Item.of('mekanism:module_teleportation_unit', 1),
    [
      'ABE',
      'CDC',
      'EBA'
    ],
    {
      A: 'mekanism:alloy_atomic',
      B: 'mekanism:ultimate_control_circuit',
      C: 'mekanism:teleportation_core',
      D: 'mekanism:module_base',
      E: 'ae2:singularity'
    }

  )

  event.replaceInput({output: 'mekanism:module_gravitational_modulating_unit'},
    'mekanism:pellet_antimatter',
    'ae2:singularity'
  )

  event.remove({output: 'mekanism:digital_miner'})
  event.recipes.shaped(
    Item.of('mekanism:digital_miner'),
    [
      'ABA',
      'CDC',
      'EFE'
    ],
    {
      A: 'mekanism:alloy_atomic',
      B: 'kubejs:energized_gooninite_block',
      C: 'gooninite:gooninite_pickaxe',
      D: 'mekanism:robit',
      E: 'mekanism:teleportation_core',
      F: 'ae2:spatial_pylon' 
    }
  )

  //AE2
  event.replaceInput({output: '#forge:ae2glass'}, 'ae2:quartz_glass', 'ae2:quartz_vibrant_glass')
  event.replaceInput(
    {not: {output: 'ae2:quartz_vibrant_glass'}, input: 'ae2:quartz_glass'}, 
    'ae2:quartz_glass', 
    'kubejs:energized_quartz_glass'
  )
  event.replaceInput({id: 'expatternprovider:cobblestone_cell'}, 'ae2:cell_component_16k', 'advanced_ae:quantum_storage_component')
  event.replaceInput({id: 'expatternprovider:cobblestone_cell'}, 'minecraft:diamond', 'gooninite:gooninite_pellet')
  event.replaceInput({id: 'expatternprovider:water_cell'}, 'ae2:cell_component_16k', 'advanced_ae:quantum_storage_component')
  event.replaceInput({id: 'expatternprovider:water_cell'}, 'minecraft:diamond', 'gooninite:gooninite_pellet')
  //event.replaceInput({output: 'advanced_ae:strength_card'}, 'minecraft:diamond_sword', 'gooninite:gooninite_sword')
  event.replaceInput({output: 'advanced_ae:attack_speed_card'},  'advanced_ae:quantum_processor', 'alexsmobs:roadrunner_boots')
  event.replaceInput({output:'advanced_ae:regeneration_card'}, 'minecraft:mushroom_stew', 'farmersdelight:pasta_with_mutton_chop')
  event.replaceInput({output: 'advanced_ae:regeneration_card'}, 'minecraft:cookie', 'farmersdelight:glow_berry_custard')
  event.replaceInput({output: 'advanced_ae:luck_card'}, 'minecraft:amethyst_block', Item.of('minecraft:potion', '{Potion:"minecraft:luck"}').strongNBT())
  event.replaceInput({output: 'advanced_ae:flight_card'}, 'minecraft:elytra', 'mekanism:hdpe_elytra')
  event.replaceInput({output: 'ae2:dense_energy_cell'}, 'ae2:calculation_processor', 'gooninite:gooninite_pellet')
  event.replaceInput({output: 'expatternprovider:assembler_matrix_frame'}, 'minecraft:lapis_lazuli', 'mekanism:ingot_steel')
  event.replaceInput({output: 'expatternprovider:assembler_matrix_frame'}, 'kubejs:energized_quartz_glass', 'kubejs:gooninite_ingot')
  event.replaceInput({output: 'expatternprovider:assembler_matrix_frame'}, 'minecraft:iron_ingot', 'kubejs:energized_quartz_glass')
  event.replaceInput({output: 'expatternprovider:wireless_hub'}, 'ae2:quantum_link', 'gooninite:gooninite_pellet')
  event.replaceInput({output: 'ae2:controller'}, 'ae2:engineering_processor', 'gooninite:gooninite_nugget')
  event.replaceInput({output: 'ae2:controller'}, 'ae2:fluix_crystal', 'ae2:calculation_processor')

  
})

