package de.nikey.elements.Commands;

import de.nikey.elements.Abilities.EarthAbilities;
import de.nikey.elements.Abilities.FireAbilities;
import de.nikey.elements.Abilities.WaterAbilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ElementCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(Component.text("Falsche verwendung", NamedTextColor.RED));
            return true;
        }

        String element = args[0].toLowerCase();
        String abilityNumber = args[1];

        switch (element) {
            case "fire" -> {
                if (abilityNumber.equals("1")) FireAbilities.loderndeWelle(player);
                else if (abilityNumber.equals("2")) FireAbilities.infernoKuppel(player);
                else sendInvalidAbility(player);
            }
            case "water" -> {
                if (abilityNumber.equals("1")) WaterAbilities.geysirAusbruch(player);
                else if (abilityNumber.equals("2")) WaterAbilities.flutkraft(player);
                else sendInvalidAbility(player);
            }
            case "earth" -> {
                if (abilityNumber.equals("1")) EarthAbilities.steinhagel(player);
                else if (abilityNumber.equals("2")) EarthAbilities.wurzelkaefig(player);
                else sendInvalidAbility(player);
            }
            default -> player.sendMessage(Component.text("Unbekanntes Element!", NamedTextColor.RED));
        }

        return true;
    }

    private void sendInvalidAbility(Player player) {
        player.sendMessage(Component.text("Ungültige Fähigkeit.", NamedTextColor.RED));
    }
}
