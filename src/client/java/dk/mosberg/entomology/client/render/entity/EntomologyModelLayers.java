package dk.mosberg.entomology.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/**
 * Registry for entity model layers used by Entomology mod.
 */
public class EntomologyModelLayers {
  public static final EntityModelLayer INSECT = new EntityModelLayer(
      Identifier.of("entomology", "insect"), "main");
  public static final EntityModelLayer FLYING_INSECT = new EntityModelLayer(
      Identifier.of("entomology", "flying_insect"), "main");
}
