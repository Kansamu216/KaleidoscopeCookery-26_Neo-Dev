package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

/**
 * Registers the mod's cooking-recipe GameTests into the vanilla test registry.
 * These run on the built-in {@code empty} structure with the default environment.
 */
@EventBusSubscriber(modid = "kaleidoscope_cookery")
public final class ModGameTests {
   private ModGameTests() {
   }

   @SubscribeEvent
   public static void register(RegisterGameTestsEvent event) {
      Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
         Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "cooking_recipe_empty"),
         new TestEnvironmentDefinition.Functions(java.util.Optional.empty(), java.util.Optional.empty())
      );
      register(event, environment, "pot_recipe_resolves_result", CookingRecipeGameTests::potRecipeResolvesResult);
      register(event, environment, "stockpot_recipe_resolves_result", CookingRecipeGameTests::stockpotRecipeResolvesResult);
      register(event, environment, "millstone_recipe_resolves_result", CookingRecipeGameTests::millstoneRecipeResolvesResult);
      register(event, environment, "chopping_board_recipe_resolves_result", CookingRecipeGameTests::choppingBoardRecipeResolvesResult);
   }

   private static void register(
      RegisterGameTestsEvent event,
      Holder<TestEnvironmentDefinition<?>> environment,
      String name,
      Consumer<GameTestHelper> function
   ) {
      Identifier id = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", name);
      TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
         environment, Identifier.withDefaultNamespace("empty"), 200, 1, true
      );
      event.registerTest(id, new FunctionInstance(data, function, net.minecraft.network.chat.Component.literal(name)));
   }
}
