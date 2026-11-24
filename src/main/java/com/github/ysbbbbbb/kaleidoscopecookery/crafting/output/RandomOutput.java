package com.github.ysbbbbbb.kaleidoscopecookery.crafting.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * A millstone output. The template is resolved into an ItemStack only when
 * used, because recipe reload happens before item components are bound.
 */
public record RandomOutput(ItemStackTemplate template, float chance) {
   public static final RandomOutput EMPTY = new RandomOutput((ItemStackTemplate) null, 1.0F);
   public static final Codec<RandomOutput> CODEC = Codec.lazyInitialized(
      () -> RecordCodecBuilder.create(
         instance -> instance.group(
               ItemStackTemplate.MAP_CODEC.forGetter(RandomOutput::template),
               Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(RandomOutput::chance)
            )
            .apply(instance, RandomOutput::new)
      )
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, RandomOutput> STREAM_CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC,
      RandomOutput::stack,
      ByteBufCodecs.FLOAT,
      RandomOutput::chance,
      RandomOutput::new
   );

   public RandomOutput(ItemStack stack, float chance) {
      this(stack.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(stack), chance);
   }

   public ItemStack stack() {
      return this.template == null ? ItemStack.EMPTY : this.template.create();
   }

   public boolean isEmpty() {
      return this.template == null;
   }

   @Override
   public float chance() {
      return Mth.clamp(this.chance, 0.0F, 1.0F);
   }
}
