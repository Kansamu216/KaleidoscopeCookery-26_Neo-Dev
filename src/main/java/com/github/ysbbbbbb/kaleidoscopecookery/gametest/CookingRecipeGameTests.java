package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * Confirms that recipe types migrated to deferred {@code ItemStackTemplate}
 * results decode from their shipped JSON into a non-empty, correctly resolved
 * ItemStack. This exercises the {@code create()} resolution path introduced by
 * the item-components migration, without depending on block interaction.
 */
public final class CookingRecipeGameTests {
   private CookingRecipeGameTests() {
   }

   public static void potRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "pot/beef_to_cooked_beef_1", Items.COOKED_BEEF, 1);
   }

   public static void stockpotRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "stockpot/beef_noodle", ModItem.BEEF_NOODLE.item(), 1);
   }

   public static void millstoneRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "millstone/green_dye_from_cactus", Items.GREEN_DYE, 2);
   }

   public static void choppingBoardRecipeResolvesResult(GameTestHelper helper) {
      assertResolves(helper, "chopping_board/bone_from_cod", Items.BONE, 2);
   }

   private static void assertResolves(GameTestHelper helper, String recipePath, Item expectedItem, int expectedCount) {
      ServerLevel level = helper.getLevel();
      RecipeManager manager = level.recipeAccess();
      ResourceKey<Recipe<?>> key = ResourceKey.create(
         Registries.RECIPE, Identifier.fromNamespaceAndPath("kaleidoscope_cookery", recipePath)
      );
      Optional<RecipeHolder<?>> holder = manager.byKey(key);
      if (holder.isEmpty()) {
         helper.fail("Migrated recipe was not loaded: " + recipePath);
         return;
      }

      ItemStack resolved = resolveResult(holder.get());
      if (resolved.isEmpty()) {
         helper.fail("Migrated recipe resolved to an empty stack: " + recipePath);
         return;
      }
      if (!resolved.is(expectedItem)) {
         helper.fail("Result item mismatch for " + recipePath + ": expected " + expectedItem + ", got " + resolved.getItem());
         return;
      }
      if (resolved.getCount() != expectedCount) {
         helper.fail("Result count mismatch for " + recipePath + ": expected " + expectedCount + ", got " + resolved.getCount());
         return;
      }
      helper.succeed();
   }

   /** Calls the type-specific result accessor so the deferred template is created. */
   private static ItemStack resolveResult(RecipeHolder<?> holder) {
      Recipe<?> recipe = holder.value();
      if (recipe instanceof PotRecipe pot) {
         return pot.result().create();
      }
      if (recipe instanceof StockpotRecipe stockpot) {
         return stockpot.result().create();
      }
      if (recipe instanceof MillstoneRecipe millstone) {
         return millstone.results().getFirst().stack();
      }
      if (recipe instanceof ChoppingBoardRecipe board) {
         return board.getResult();
      }
      return ItemStack.EMPTY;
   }

   /** Lazily resolves a mod item so the test does not run during class init. */
   private enum ModItem {
      BEEF_NOODLE("beef_noodle");

      private final String path;

      ModItem(String path) {
         this.path = path;
      }

      Item item() {
         return net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", this.path)
         );
      }
   }
}
