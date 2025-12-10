package dk.mosberg.entomology.client.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dk.mosberg.entomology.client.config.EntomologyConfigScreen;

/**
 * Integration with Mod Menu for config screen access.
 */
public class ModMenuIntegration implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return EntomologyConfigScreen::new;
  }
}
