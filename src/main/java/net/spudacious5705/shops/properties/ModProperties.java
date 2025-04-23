package net.spudacious5705.shops.properties;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public class ModProperties {
    public static final EnumProperty<Colour> CUSHION_COLOUR = EnumProperty.of("colour", Colour.class);
    public static final BooleanProperty BREAKABLE = BooleanProperty.of("breakable");

    public static void registerModProperties(){}
}
