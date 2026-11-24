package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.mojang.serialization.MapCodec;

/**
 * A GameTest instance that holds its test function directly, avoiding a lookup
 * in the TEST_FUNCTION registry during the test run.
 */
public final class FunctionInstance extends GameTestInstance {
   private final Consumer<GameTestHelper> function;
   private final Component description;

   public FunctionInstance(
      TestData<Holder<TestEnvironmentDefinition<?>>> data,
      Consumer<GameTestHelper> function,
      Component description
   ) {
      super(data);
      this.function = function;
      this.description = description;
   }

   @Override
   public void run(GameTestHelper helper) {
      this.function.accept(helper);
   }

   @Override
   public MapCodec<? extends GameTestInstance> codec() {
      return MapCodec.unit(this);
   }

   @Override
   protected MutableComponent typeDescription() {
      return Component.literal("kaleidoscope_cookery recipe test");
   }

   @Override
   public Component describe() {
      return this.description;
   }
}
