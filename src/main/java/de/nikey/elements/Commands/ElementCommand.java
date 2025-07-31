package de.nikey.elements.Commands;

import de.nikey.elements.Abilities.EarthAbilities;
import de.nikey.elements.Abilities.ElementType;
import de.nikey.elements.Abilities.FireAbilities;
import de.nikey.elements.Abilities.WaterAbilities;
import de.nikey.elements.Elements;
import de.nikey.elements.Managers.JuggernautManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        JuggernautManager jm = Elements.getPlugin().getJuggernautManager();

        switch (element) {
            case "fire" -> {
                if (jm.isJuggernaut(player.getUniqueId()) && jm.getElement(player.getUniqueId()) == ElementType.FIRE) {
                    if (abilityNumber.equals("1")) FireAbilities.loderndeWelle(player);
                    else if (abilityNumber.equals("2")) FireAbilities.infernoKuppel(player);
                    else sendInvalidAbility(player);
                }
            }
            case "water" -> {
                if (jm.isJuggernaut(player.getUniqueId()) && jm.getElement(player.getUniqueId()) == ElementType.WATER) {
                    if (abilityNumber.equals("1")) WaterAbilities.geysirAusbruch(player);
                    else if (abilityNumber.equals("2")) WaterAbilities.flutkraft(player);
                    else sendInvalidAbility(player);
                }
            }
            case "earth" -> {
                if (jm.isJuggernaut(player.getUniqueId()) && jm.getElement(player.getUniqueId()) == ElementType.EARTH) {
                    if (abilityNumber.equals("1")) EarthAbilities.steinhagel(player);
                    else if (abilityNumber.equals("2")) EarthAbilities.wurzelkaefig(player);
                    else sendInvalidAbility(player);
                }
            }
            case "setup" -> {
                if (!player.isOp())return true;
                List<Player> players = new ArrayList<>();
                for (UUID uuid : Elements.getPlugin().getJuggernautManager().getAllJuggernauts().keySet()) {
                    Player t = Bukkit.getPlayer(uuid);
                    if (t == null)continue;
                    players.add(t);
                }

                Elements.getPlugin().getJuggernautManager().setupJuggernautTeam(players);
                player.sendMessage(Component.text("Setup complete"));
            }

            case "reload" -> {
                if (player.isOp()) {
                    Elements.getPlugin().reloadConfig();
                    player.sendMessage(Component.text("Reloaded config!").color(NamedTextColor.GREEN));
                }
            }
            default -> player.sendMessage(Component.text("Unbekanntes Element!", NamedTextColor.RED));
        }

        return true;
    }

    private void sendInvalidAbility(Player player) {
        player.sendMessage(Component.text("Ungültige Fähigkeit.", NamedTextColor.RED));
    }
}
