/*
 * Copyright (c) 2019-2022 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.world.gen.surfacebuilder;

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.world.biome.GalacticraftBiomeKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MoonSurfaceRules {
    private static final ConditionSource IS_MARE = biome(GalacticraftBiomeKey.Moon.MARE, GalacticraftBiomeKey.Moon.MARE_EDGE, GalacticraftBiomeKey.Moon.MARE_FLAT, GalacticraftBiomeKey.Moon.MARE_HILLS, GalacticraftBiomeKey.Moon.MARE_VALLEY);
    private static final ConditionSource IS_HIGHLANDS = biome(GalacticraftBiomeKey.Moon.HIGHLANDS, GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE, GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT, GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS, GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY);

    private static final RuleSource BEDROCK = block(Blocks.BEDROCK);
    private static final RuleSource LUNASLATE = block(GalacticraftBlock.LUNASLATE);
    private static final RuleSource MOON_DIRT = block(GalacticraftBlock.MOON_DIRT);
    private static final RuleSource MOON_TURF = block(GalacticraftBlock.MOON_TURF);
    private static final RuleSource MOON_BASALT = block(GalacticraftBlock.MOON_BASALT);
    private static final RuleSource DEBUG_STATE = block(GalacticraftBlock.ALUMINUM_DECORATION);

    private static final RuleSource SECONDARY_MATERIAL = SurfaceRules.sequence(
            SurfaceRules.ifTrue(IS_MARE, MOON_BASALT),
            SurfaceRules.ifTrue(IS_HIGHLANDS, MOON_DIRT),
            DEBUG_STATE
    );
    private static final RuleSource SURFACE_MATERIAL = SurfaceRules.sequence(
            SurfaceRules.ifTrue(IS_MARE, MOON_BASALT),
            SurfaceRules.ifTrue(IS_HIGHLANDS, MOON_TURF),
            DEBUG_STATE
    );
    private static final RuleSource SURFACE_GENERATION = SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SURFACE_MATERIAL),
            SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SECONDARY_MATERIAL)
    );

    @Contract("_ -> new")
    private static @NotNull RuleSource block(@NotNull Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static @NotNull RuleSource createDefaultRule() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SURFACE_GENERATION),
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("lunaslate", VerticalAnchor.absolute(-4), VerticalAnchor.absolute(4)), LUNASLATE)
        );
    }

    @Contract("_ -> new")
    public static SurfaceRules.@NotNull ConditionSource biome(@NotNull TagKey<Biome> biome) {
        return new BiomeTagRule(biome);
    }

    @SafeVarargs
    @Contract("_ -> new")
    public static SurfaceRules.@NotNull ConditionSource biome(@NotNull ResourceKey<Biome> @NotNull... keys) {
        return SurfaceRules.isBiome(keys);
    }
}
